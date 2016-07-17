package pl.itcity.cg.desktop.backend.files.model;

import java.util.Date;

/**
 * @author Michal Adamczyk
 */
public class FileMeta {

    private Checksum checksum;

    private String fileinfoId;

    private String documentInfoId;

    private String fileName;

    private Date modifiedDate;

    private Date createdDate;

    private String symbol;

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

    public String getDocumentInfoId() {
        return documentInfoId;
    }

    public void setDocumentInfoId(String documentInfoId) {
        this.documentInfoId = documentInfoId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
