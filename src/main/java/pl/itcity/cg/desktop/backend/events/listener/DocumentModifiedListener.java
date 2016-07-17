package pl.itcity.cg.desktop.backend.events.listener;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import pl.itcity.cg.desktop.backend.files.ChecksumUtil;
import pl.itcity.cg.desktop.backend.files.FileConstants;
import pl.itcity.cg.desktop.backend.files.events.FileModifiedEvent;
import pl.itcity.cg.desktop.backend.files.model.Checksum;
import pl.itcity.cg.desktop.backend.files.model.FileMeta;
import pl.itcity.cg.desktop.backend.service.ServiceInvoker;
import pl.itcity.cg.desktop.model.FileInfo;

/**
 * Listener for file modified event
 *
 * @author Michal Adamczyk
 */
@Component
public class DocumentModifiedListener implements ApplicationListener<FileModifiedEvent>{

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentModifiedListener.class);

    @Resource
    private ServiceInvoker serviceInvoker;

    @Override
    public void onApplicationEvent(FileModifiedEvent event) {
        Path path = event.getPath();
        LOGGER.info("path modified: " + path);
        Path metaPath = Paths.get(path.toAbsolutePath()
                                       .toString() + FileConstants.META);
        File metaFile = metaPath.toFile();
        if (metaFile.exists()) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                LOGGER.info("about to recalculate checksum for path: " + path);
                FileMeta fileMeta = objectMapper.readValue(metaFile, FileMeta.class);
                byte[] newChecksum = ChecksumUtil.calculateChecksum(path, ChecksumUtil.MD5);
                fileMeta.setModifiedDate(event.getEventDate());
                fileMeta.setChecksum(Checksum.valueOf(ChecksumUtil.MD5, newChecksum, new Date()));
                objectMapper.writeValue(metaFile, fileMeta);
                LOGGER.info("checksum recalculted for path: " + path);

                ResponseEntity<FileInfo> updatedFileInfo = reuploadFile(path, fileMeta);
                FileInfo body = updatedFileInfo.getBody();
                LOGGER.info("fileInfo updated: " + body);
                fileMeta.setFileinfoId(body.getId());
                fileMeta.setSymbol(body.getSymbol());
                objectMapper.writeValue(metaFile, fileMeta);
            } catch (IOException e) {
                LOGGER.error("error while processing file change:", e);
            }
        }
    }

    /**
     * reuploads file
     *
     * @param path
     *         path
     * @param fileMeta
     *         file metadata
     * @return updated file metadata
     * @throws MalformedURLException
     */
    private ResponseEntity<FileInfo> reuploadFile(Path path, FileMeta fileMeta) throws MalformedURLException {
        HttpHeaders headers = serviceInvoker.getAuthHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> multipartMap = new LinkedMultiValueMap<>();
        multipartMap.add("file", new UrlResource(path.toAbsolutePath()
                                                         .toUri()));
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(multipartMap, headers);
        return serviceInvoker.exchange("/document/" + fileMeta.getDocumentInfoId() + "/reupload/" + fileMeta.getFileinfoId(), HttpMethod.POST, requestEntity, FileInfo.class);
    }
}
