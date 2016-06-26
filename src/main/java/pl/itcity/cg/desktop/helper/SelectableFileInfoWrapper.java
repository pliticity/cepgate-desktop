package pl.itcity.cg.desktop.helper;

import pl.itcity.cg.desktop.model.FileInfo;

/**
 * @author Michal Adamczyk
 */
public class SelectableFileInfoWrapper {

    private boolean selected;

    private final FileInfo fileInfo;

    public SelectableFileInfoWrapper(FileInfo fileInfo) {
        this.fileInfo = fileInfo;
    }

    public boolean isSelected() {
        return selected;
    }

    public FileInfo getFileInfo() {
        return fileInfo;
    }
}
