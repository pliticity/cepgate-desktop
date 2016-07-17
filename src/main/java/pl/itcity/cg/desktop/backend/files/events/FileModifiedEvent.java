package pl.itcity.cg.desktop.backend.files.events;

import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;

/**
 * Event for file modification
 *
 * @author Michal Adamczyk
 */
public class FileModifiedEvent extends FilesystemEvent {

    /**
     * constructor initializing necessary fields
     *
     * @param source
     *         event source
     * @param path
     *         path
     */
    public FileModifiedEvent(Object source, Path path) {
        super(source, path, StandardWatchEventKinds.ENTRY_MODIFY);
    }
}
