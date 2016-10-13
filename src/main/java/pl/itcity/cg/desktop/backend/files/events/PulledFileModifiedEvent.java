package pl.itcity.cg.desktop.backend.files.events;

import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;

/**
 *
 * @author Patryk Majchrzycki
 */
public class PulledFileModifiedEvent extends FilesystemEvent {

    private String dicId;

    private String fileId;

    public PulledFileModifiedEvent(Object source, Path path, String dicId, String fileId) {
        super(source, path, StandardWatchEventKinds.ENTRY_MODIFY);
        this.dicId = dicId;
        this.fileId=fileId;
    }

    public String getDicId() {
        return dicId;
    }

    public String getFileId() {
        return fileId;
    }
}
