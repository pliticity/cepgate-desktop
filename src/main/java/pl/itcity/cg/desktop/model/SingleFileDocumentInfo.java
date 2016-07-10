package pl.itcity.cg.desktop.model;

/**
 * Bean containing file info and corresponding document info
 *
 * @author Michal Adamczyk, HYCOM S.A.
 */
public class SingleFileDocumentInfo {
    private final DocumentInfo documentInfo;
    private final FileInfo fileInfo;

    /**
     * constructor initializing necessary fields
     *
     * @param documentInfo
     *         document info, not null
     * @param fileInfo
     *         file info, not null
     */
    public SingleFileDocumentInfo(DocumentInfo documentInfo, FileInfo fileInfo) {
        this.fileInfo = fileInfo;
        this.documentInfo = documentInfo;
    }

    public DocumentInfo getDocumentInfo() {
        return documentInfo;
    }

    public FileInfo getFileInfo() {
        return fileInfo;
    }
}
