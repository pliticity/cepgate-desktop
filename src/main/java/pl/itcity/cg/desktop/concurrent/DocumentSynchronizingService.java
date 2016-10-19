package pl.itcity.cg.desktop.concurrent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.zeroturnaround.zip.ZipUtil;

import javafx.concurrent.Task;
import pl.itcity.cg.desktop.backend.files.SynchronizingTempResourceFileVisitor;
import pl.itcity.cg.desktop.configuration.ConfigManager;
import pl.itcity.cg.desktop.configuration.model.AppConfig;
import pl.itcity.cg.desktop.model.DocumentInfo;
import pl.itcity.cg.desktop.model.FileInfo;
import pl.itcity.cg.desktop.model.SingleFileDocumentInfo;

/**
 * Service that enables synchronization of document infos. Downloads all available files and puts them into proper file structure
 *
 * @author Michal Adamczyk
 */
@Component
@Scope("prototype")
public class DocumentSynchronizingService extends BaseRestService<Void>{

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentSynchronizingService.class);

    /**
     * list of documents to synchronize
     */
    private List<DocumentInfo> documents;

    @Resource
    private ConfigManager configManager;

    @Override
    protected Task<Void> createTask() {

        return new Task<Void>() {

            private final List<DocumentInfo> taskDocuments = documents;

            private long progress = 0L;
            @Override
            protected Void call() throws Exception {
                List<DocumentInfo> documentsWithFiles = documents.stream()
                        .filter(documentInfo -> Optional.ofNullable(documentInfo.getFiles())
                                .map(fileInfos -> fileInfos.size() > 0)
                                .orElse(false))
                        .collect(Collectors.toList());
                long filesCount = documentsWithFiles.stream()
                        .map(DocumentInfo::getFiles)
                        .flatMap(Collection::stream)
                        .count();
                updateProgress(0L, filesCount);
                AppConfig appConfig = configManager.getAppConfig();
                String syncDirectory = appConfig.getSyncDirectory();
                documentsWithFiles.stream()
                        .map(documentInfo -> documentInfo.getFiles()
                                .stream()
                                .map(fileInfo -> new SingleFileDocumentInfo(documentInfo, fileInfo)))
                        .flatMap(Function.identity())
                        .forEach(singleFileDocInfo -> {
                            if (StringUtils.isBlank(singleFileDocInfo.getFileInfo()
                                                            .getSymbol())) {
                                String[] fileIds = {singleFileDocInfo.getFileInfo().getId()};
                                HttpEntity<String[]> httpEntity = new HttpEntity<>(fileIds, serviceInvoker.getAuthHeaders());
                                ResponseEntity<FileInfo> resultFileInfoResponse = serviceInvoker.exchange("files", HttpMethod.POST, httpEntity, FileInfo.class);
                                if (HttpStatus.OK.equals(resultFileInfoResponse.getStatusCode())) {
                                    FileInfo resultFileInfo = resultFileInfoResponse.getBody();
                                    processSingleFileDownload(filesCount, syncDirectory, singleFileDocInfo, resultFileInfo, true);
                                }
                            } else {
                                processSingleFileDownload(filesCount, syncDirectory, singleFileDocInfo, singleFileDocInfo.getFileInfo(), false);
                            }
                        });
                return null;
            }

            /**
             * performs single file download. Calls file download request and synchronizes result
             *
             * @param filesCount
             *         files count
             * @param syncDirectory
             *         sunchronize directory
             * @param singleFileDocInfo
             *         single file document info
             * @param resultFileInfo
             *         result file info
             * @param unzip
             *         should result be unzipped flag
             */
            private void processSingleFileDownload(long filesCount, String syncDirectory, SingleFileDocumentInfo singleFileDocInfo, FileInfo resultFileInfo, boolean unzip) {
                ResponseEntity<byte[]> downloadedFileResponse = callDownloadSingleFile(resultFileInfo, false);
                if (HttpStatus.OK.equals(downloadedFileResponse.getStatusCode())) {
                    try {
                        synchronizeSingleFileContent(syncDirectory, singleFileDocInfo, downloadedFileResponse, unzip);
                        progress = progress + 1;
                        updateProgress(progress, filesCount);
                    } catch (IOException e) {
                        LOGGER.error("unable to process files", e);
                    }
                }
            }

            /**
             * handles synchronization of single file content
             *
             * @param syncDirectory
             *         synchronization directory (destination for files)
             * @param singleFileDocInfo
             *         single file and document info object
             * @param downloadedFileResponse
             *         downloaded file response
             * @param unzip
             *         should output be unzipped
             * @throws IOException
             */
            private void synchronizeSingleFileContent(String syncDirectory, SingleFileDocumentInfo singleFileDocInfo, ResponseEntity<byte[]> downloadedFileResponse, boolean unzip) throws
                                                                                                                                                                     IOException {
                Path tempDirectory = Files.createTempDirectory(UUID.randomUUID()
                                                                       .toString());
                if (unzip) {
                    InputStream byteArrayInputStream = new ByteArrayInputStream(downloadedFileResponse.getBody());
                    ZipUtil.unpack(byteArrayInputStream, tempDirectory.toFile());
                } else {
                    String destinationFileName = readFilenameFromResponseHeaders(singleFileDocInfo, downloadedFileResponse);
                    Path resolve = tempDirectory.resolve(destinationFileName);
                    LOGGER.info("writing response bytes to file: " + resolve);
                    Files.write(resolve, downloadedFileResponse.getBody());
                }
                Files.walkFileTree(tempDirectory, new SynchronizingTempResourceFileVisitor(singleFileDocInfo, syncDirectory));
                FileUtils.deleteDirectory(tempDirectory.toFile());
            }

            /**
             * reads file name from response headers. Returns name from fileInfo if not found
             *
             * @param singleFileDocInfo
             *         single document file info
             * @param downloadedFileResponse
             *         downloader file response
             * @return file name from header or from fileinfo if not found
             */
            private String readFilenameFromResponseHeaders(SingleFileDocumentInfo singleFileDocInfo, ResponseEntity<byte[]> downloadedFileResponse) {
                return Optional.ofNullable(downloadedFileResponse.getHeaders()
                                                   .get("Content-Disposition"))
                        .orElse(Collections.emptyList())
                        .stream()
                        .findFirst()
                        .map(filenameHeader -> {
                            Optional<String> filename = Arrays.stream(filenameHeader.split(";"))
                                    .filter(s -> s.contains("filename"))
                                    .findFirst();
                            return filename.map(filenameHeaderValue -> {
                                LOGGER.debug("filename header obtained: " + filenameHeader);
                                String[] splittedFilenameheader = filenameHeaderValue.split("=");
                                if (splittedFilenameheader.length == 2) {
                                    return splittedFilenameheader[1].replace("\"", "");
                                } else {
                                    LOGGER.warn("no filenme found in response header, returning name from fileInfo");
                                    return singleFileDocInfo.getFileInfo()
                                            .getName();
                                }
                            })
                                    .orElse(singleFileDocInfo.getFileInfo()
                                                    .getName());
                        })
                        .orElse(singleFileDocInfo.getFileInfo()
                                        .getName());
            }

            /**
             * downloads single file
             *
             * @param resultFileInfo
             *         file info for file to download
             * @param isTemp is temporary file flag
             * @return downloaded file response entity
             */
            private ResponseEntity<byte[]> callDownloadSingleFile(FileInfo resultFileInfo, boolean isTemp) {
                HttpHeaders headers = serviceInvoker.getAuthHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
                HttpEntity<byte[]> downloadFileEnity = new HttpEntity<>(headers);
                String tempAttributeValue = isTemp ? "?temp=true" : StringUtils.EMPTY;
                return serviceInvoker.exchange("file/" + resultFileInfo.getSymbol() + tempAttributeValue, HttpMethod.GET, downloadFileEnity, byte[].class);
            }
        };
    }

    /**
     * extracts file ids from document info
     *
     * @param documentInfo
     *         document info
     * @return array of file ids
     */
    private String[] extractFIleIds(DocumentInfo documentInfo) {
        return documentInfo.getFiles()
                .stream()
                .map(FileInfo::getId)
                .collect(Collectors.toList())
                .toArray(new String[0]);
    }

    public List<DocumentInfo> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentInfo> documents) {
        this.documents = documents;
    }
}
