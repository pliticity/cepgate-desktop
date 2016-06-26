package pl.itcity.cg.desktop.model;

import java.util.Objects;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author Michal Adamczyk
 */
public class Classification {

    private String classificationId;

    private String name;

    public String getClassificationId() {
        return classificationId;
    }

    public void setClassificationId(String classificationId) {
        this.classificationId = classificationId;
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
        if (!(o instanceof Classification)) {
            return false;
        }
        Classification that = (Classification) o;
        return Objects.equals(classificationId, that.classificationId) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(classificationId, name);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("classificationId", classificationId)
                .append("name", name)
                .toString();
    }
}
