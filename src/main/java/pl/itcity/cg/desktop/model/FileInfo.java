package pl.itcity.cg.desktop.model;

import java.util.Objects;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author Michal Adamczyk
 */
public class FileInfo {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FileInfo)) {
            return false;
        }
        FileInfo fileInfo = (FileInfo) o;
        return Objects.equals(id, fileInfo.id) && Objects.equals(name, fileInfo.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id)
                .append("name", name)
                .toString();
    }
}
