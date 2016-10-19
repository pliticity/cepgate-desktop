package pl.itcity.cg.desktop.model;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author Michal Adamczyk
 */
public class DocumentInfo {

    private String id;
    private Classification classification;
    private String documentNumber;
    private String documentName;
    private DocumentType type;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date creationDate;
    private Principal createdBy;
    private List<FileInfo> files;
    private List<Tag> tags;
    private List<DocumentActivity> activities;
    private List<Link> links;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Classification getClassification() {
        return classification;
    }

    public void setClassification(Classification classification) {
        this.classification = classification;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public DocumentType getType() {
        return type;
    }

    public void setType(DocumentType type) {
        this.type = type;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Principal getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Principal createdBy) {
        this.createdBy = createdBy;
    }

    public List<FileInfo> getFiles() {
        return files;
    }

    public void setFiles(List<FileInfo> files) {
        this.files = files;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<DocumentActivity> getActivities() {
        return activities;
    }

    public void setActivities(List<DocumentActivity> activities) {
        this.activities = activities;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DocumentInfo)) {
            return false;
        }
        DocumentInfo that = (DocumentInfo) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(classification, that.classification) &&
                Objects.equals(documentNumber, that.documentNumber) &&
                Objects.equals(documentName, that.documentName) &&
                type == that.type &&
                Objects.equals(creationDate, that.creationDate) &&
                Objects.equals(createdBy, that.createdBy) &&
                Objects.equals(files, that.files) &&
                Objects.equals(tags, that.tags) &&
                Objects.equals(activities, that.activities) &&
                Objects.equals(links, that.links);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, classification, documentNumber, documentName, type, creationDate, createdBy, files, tags, activities, links);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id)
                .append("classification", classification)
                .append("documentNumber", documentNumber)
                .append("documentName", documentName)
                .append("type", type)
                .append("creationDate", creationDate)
                .append("createdBy", createdBy)
                .append("files", files)
                .append("tags", tags)
                .append("activities", activities)
                .append("links", links)
                .toString();
    }
}
