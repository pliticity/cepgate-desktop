package pl.itcity.cg.desktop.backend.files.model;

import java.util.Date;

/**
 * @author Michal Adamczyk
 */
public class FileMeta {

    private Checksum checksum;

    private String fileinfoId;

    private String fileName;

    private Date modifiedDate;

    private Date createdDate;

    public Checksum getChecksum() {
        return checksum;
    }

    public void setChecksum(Checksum checksum) {
        this.checksum = checksum;
    }

    public String getFileinfoId() {
        return fileinfoId;
    }

    public void setFileinfoId(String fileinfoId) {
        this.fileinfoId = fileinfoId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
