package pl.itcity.cg.desktop.backend.files.events;

import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;

/**
 *
 * @author Patryk Majchrzycki
 */
public class PulledFileModifiedEvent extends FilesystemEvent {

    private String dicId;

    public PulledFileModifiedEvent(Object source, Path path, String dicId) {
        super(source, path, StandardWatchEventKinds.ENTRY_MODIFY);
        this.dicId = dicId;
    }

    public String getDicId() {
        return dicId;
    }
}
