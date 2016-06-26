package pl.itcity.cg.desktop.model;

import java.util.Objects;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author Michal Adamczyk
 */
public class Tag {
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tag)) {
            return false;
        }
        Tag tag = (Tag) o;
        return Objects.equals(text, tag.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("text", text)
                .toString();
    }
}
