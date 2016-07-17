package pl.itcity.cg.desktop.backend.files.events;

import static pl.itcity.cg.desktop.backend.files.FileConstants.META;
import static pl.itcity.cg.desktop.backend.files.FileConstants.META_EXTENSTION;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

/**
 * Filesystem events factory accepting paths corresponding to documents from current working directory.
 *
 * Accept implementation schould be FAST since it locks the directory watcher thread
 *
 * @author Michal Adamczyk
 */
public class DocumentFilesystemEventsFactory implements FilesystemEventFactory {

    private final Path currentDirectory;

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentFilesystemEventsFactory.class);

    /**
     * constructor initializing necessary fields
     *
     * @param currentDirectory
     *         current directory, not null
     */
    public DocumentFilesystemEventsFactory(Path currentDirectory) {
        Preconditions.checkArgument(currentDirectory != null, "currentDirectory can not be null");
        this.currentDirectory = currentDirectory;
    }

    @Override
    public FilesystemEvent filesystemEvent(Object owner, Path path, WatchEvent.Kind kind) {
        Path resolvedPath = currentDirectory.resolve(path);
        if (StandardWatchEventKinds.ENTRY_MODIFY.equals(kind)) {
            return new FileModifiedEvent(owner, resolvedPath);
        }
        return new FilesystemEvent(owner, resolvedPath, kind);
    }

    @Override
    public boolean acceptPath(Path path) {
        LOGGER.debug("current directory: " + currentDirectory + " checking path: " + path);
        String extension = FilenameUtils.getExtension(path.toString());
        boolean isNotMeta = !META_EXTENSTION.equals(extension);
        LOGGER.debug("is given file *.meta = " + !isNotMeta);
        if (!isNotMeta){
            return false;
        }
        boolean metaExists = metaExists(currentDirectory.resolve(path));
        LOGGER.debug("meta exists = " + metaExists);
        return metaExists;
    }

    /**
     * checks if meta file exists for given path
     *
     * @param path
     *         patch to check
     * @return true iff meta exists for given path
     */
    private boolean metaExists(Path path) {
        Path metaPath = Paths.get(path.toAbsolutePath()
                                       .toString() + META);
        LOGGER.debug("metaPath = "+metaPath);
        return metaPath
                .toFile()
                .exists();
    }
}
