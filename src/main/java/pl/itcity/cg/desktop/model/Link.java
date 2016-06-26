package pl.itcity.cg.desktop.model;

import java.util.Objects;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author Michal Adamczyk
 */
public class Link {
    private String documentId;

    private String documentName;

    private LinkType linkType;

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public LinkType getLinkType() {
        return linkType;
    }

    public void setLinkType(LinkType linkType) {
        this.linkType = linkType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Link)) {
            return false;
        }
        Link link = (Link) o;
        return Objects.equals(documentId, link.documentId) &&
                Objects.equals(documentName, link.documentName) &&
                linkType == link.linkType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(documentId, documentName, linkType);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("documentId", documentId)
                .append("documentName", documentName)
                .append("linkType", linkType)
                .toString();
    }
}
