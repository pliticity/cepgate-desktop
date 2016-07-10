package pl.itcity.cg.desktop.backend.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;

import pl.itcity.cg.desktop.backend.files.model.Checksum;
import pl.itcity.cg.desktop.backend.files.model.FileMeta;
import pl.itcity.cg.desktop.model.DocumentInfo;
import pl.itcity.cg.desktop.model.SingleFileDocumentInfo;

/**
 * Simple file visitor copying temporary resources into document-info based directory structure
 * @author Michal Adamczyk
 */
public class SynchronizingTempResourceFileVisitor implements FileVisitor<Path>{

    private static final Logger LOGGER = LoggerFactory.getLogger(SynchronizingTempResourceFileVisitor.class);
    private static final String MD5 = "MD5";

    private final Path destinationDirectory;
    private final SingleFileDocumentInfo singleFileDocumentInfo;

    private final String syncDirectory;

    /**
     * constructor initializing necessary fields
     *
     * @param singleFileDocumentInfo
     *         single document info
     * @param syncDirectory
     *         directory to synchronize (destination for files)
     */
    public SynchronizingTempResourceFileVisitor(SingleFileDocumentInfo singleFileDocumentInfo, String syncDirectory) {
        Preconditions.checkNotNull(singleFileDocumentInfo);
        Preconditions.checkArgument(StringUtils.isNotBlank(syncDirectory), "syncDirectory can not be empty");
        this.singleFileDocumentInfo = singleFileDocumentInfo;
        this.syncDirectory = syncDirectory;
        DocumentInfo documentInfo = singleFileDocumentInfo.getDocumentInfo();
        String clasificationName = documentInfo.getClassification()
                .getName();
        this.destinationDirectory = Paths.get(this.syncDirectory, clasificationName, documentInfo.getDocumentNumber(), documentInfo.getDocumentName());
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (!Files.exists(destinationDirectory)) {
            destinationDirectory.toFile()
                    .mkdirs();
        }
        Path fileName = file.getFileName();
        Path destinationFilePath = destinationDirectory.resolve(fileName);
        FileMeta fileMeta = createFileMeta(file, destinationFilePath);
        File metaFile = destinationDirectory.resolve(fileName + ".meta")
                .toFile();
        if (!metaFile.exists()){
            LOGGER.info("moving file " + fileName + " to destination directory " + destinationDirectory);
            metaFile.createNewFile();
            moveFileAndWriteMeta(file, destinationFilePath, fileMeta, metaFile);
        } else {
            LOGGER.info("metadata exists for given file: " + destinationFilePath);
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                FileMeta existingFileMeta = objectMapper.readValue(metaFile, FileMeta.class);
                if (fileMeta.getFileinfoId().equals(existingFileMeta.getFileinfoId())){
                    handleFileDuplicate(file, destinationFilePath, fileMeta, metaFile, existingFileMeta);
                }
            } catch (IOException e) {
                LOGGER.error("unable to read old meta file:", e);
                LOGGER.info("ignoring old meta");
                moveFileAndWriteMeta(file, destinationFilePath, fileMeta, metaFile);
            }
        }
        return FileVisitResult.CONTINUE;
    }

    /**
     * handles file duplicate. Creates backup for modified file.
     *
     * @param file
     *         path of given file
     * @param destinationFilePath
     *         destination file path
     * @param fileMeta
     *         given file metadata obiect
     * @param metaFile
     *         file corresponding to metadata object
     * @param existingFileMeta
     *         existing file metadata object
     * @throws IOException
     */
    private void handleFileDuplicate(Path file, Path destinationFilePath, FileMeta fileMeta, File metaFile, FileMeta existingFileMeta) throws
                                                                                                                                       IOException {
        LOGGER.info("file for fileInfo with id=" + fileMeta.getFileinfoId() + " already exists");
        if (fileMeta.getChecksum()
                .isSameAs(existingFileMeta.getChecksum())) {
            LOGGER.info("existing file and synchronized one are equal with respect to checksum - skipping");
        } else {
            if (existingFileMeta.getModifiedDate() != null) {
                handleExistingModifiedFile(existingFileMeta);

            } else {
                LOGGER.warn("existing file was not modified and will be overwritten");
            }
            LOGGER.info("moving file" + file.getFileName() + "to destination directory " + destinationDirectory);
            moveFileAndWriteMeta(file, destinationFilePath, fileMeta, metaFile);
        }
    }

    /**
     * handles existing file that was modified. If corresponding file exists, backup is created
     *
     * @param existingFileMeta
     *         existing file meta
     * @throws IOException
     */
    private void handleExistingModifiedFile(FileMeta existingFileMeta) throws IOException {
        //fixme [michal.adamczyk] until we get uploadDate, we can not do anything but overwrite existing file
        LOGGER.warn("existing file was modified after previous synchronization. CREATING BACKUP?");
        String existingFileName = existingFileMeta.getFileName();
        Path existingFilePath = Paths.get(existingFileName);
        File existingFile = existingFilePath.toFile();
        if (existingFile.exists()) {
            prepareBackup(existingFileMeta, existingFilePath);
        } else {
            LOGGER.warn("metafile exists for " + existingFileName + " but file does not exist. Existing meta will be overwritten");
        }
    }

    /**
     * prepares backup for existing modified file
     *
     * @param existingFileMeta
     *         existing file meta
     * @param existingFilePath
     *         existing file path
     * @throws IOException
     */
    private void prepareBackup(FileMeta existingFileMeta, Path existingFilePath) throws IOException {
        LOGGER.info("about to create backup file");
        String backupFilePath = existingFilePath.toString() + ".bak";
        existingFileMeta.setFileName(backupFilePath);
        File destinationMetaFile = Paths.get(backupFilePath + ".meta")
                .toFile();
        if (!destinationMetaFile.exists()) {
            destinationMetaFile.createNewFile();
        }
        moveFileAndWriteMeta(existingFilePath, Paths.get(backupFilePath), existingFileMeta, destinationMetaFile);
        LOGGER.info("backup file: " + backupFilePath + " created, metadata moved");
    }

    /**
     * moves file to destination file path and writes corresponding file meta
     *
     * @param file
     *         file
     * @param destinationFilePath
     *         destination file path
     * @param fileMeta
     *         file for metafile
     * @param metaFile
     *         file metadata object
     * @throws IOException
     */
    private void moveFileAndWriteMeta(Path file, Path destinationFilePath, FileMeta fileMeta, File metaFile) throws
                                                                                                             IOException {
        Files.move(file, destinationFilePath, StandardCopyOption.REPLACE_EXISTING);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(metaFile, fileMeta);
    }

    /**
     * creates file meta for file given by path
     *
     * @param file
     *         file path
     * @param destinationFilePath
     *         destination file path
     * @return file metadata
     * @throws IOException
     */
    private FileMeta createFileMeta(Path file, Path destinationFilePath) throws IOException {
        byte[] checksum = calculateChecksum(file, MD5);
        Checksum md5 = Checksum.valueOf(MD5, checksum, new Date());
        FileMeta fileMeta = new FileMeta();
        fileMeta.setChecksum(md5);
        fileMeta.setFileinfoId(singleFileDocumentInfo.getFileInfo()
                                       .getId());
        fileMeta.setFileName(destinationFilePath.toString());
        fileMeta.setCreatedDate(new Date());
        return fileMeta;
    }

    /**
     * calculates hash of given file with given algorythm.
     *
     * @param file
     *         path to file
     * @param algorythm
     *         algorythm
     * @return calculated checksum or empty byte array if checksum algorythm is not supported
     * @throws IOException
     */
    private byte[] calculateChecksum(Path file, String algorythm) throws IOException {
        byte[] checksum = new byte[0];
        try {
            MessageDigest digest = MessageDigest.getInstance(algorythm);
            try (FileInputStream fileInputStream = new FileInputStream(file.toFile())) {
                readBytesAndUpdateDigest(digest, fileInputStream);
            }
            return digest.digest();

        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("unable to calculate checksum", e);
        }
        return checksum;
    }

    /**
     * reads bytes of fileInputStream and updates digest
     *
     * @param digest
     *         digest
     * @param fileInputStream
     *         input stream
     * @throws IOException
     */
    private void readBytesAndUpdateDigest(MessageDigest digest, FileInputStream fileInputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int numRead;
        do {
            numRead = fileInputStream.read(buffer);
            if (numRead > 0) {
                digest.update(buffer, 0, numRead);
            }
        } while (numRead != -1);
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        LOGGER.error("exception while visiting file:", exc);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }
}
