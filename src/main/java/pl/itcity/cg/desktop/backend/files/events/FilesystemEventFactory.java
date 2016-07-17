package pl.itcity.cg.desktop.backend.files.events;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * filesystem event factory
 *
 * @author Michal Adamczyk
 */
public interface FilesystemEventFactory {

    /**
     * creates filesystem event for given path and kind
     *
     * @param owner
     *         owner
     * @param path
     *         path, not null
     * @param kind
     *         kind, not null
     * @return created event
     */
    FilesystemEvent filesystemEvent(Object owner, Path path, WatchEvent.Kind kind);

    /**
     * checks if given path is accepted for this factory
     *
     * @param path
     *         path
     * @return true if path is accepted
     */
    boolean acceptPath(Path path);
}
