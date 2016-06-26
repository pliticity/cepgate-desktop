package pl.itcity.cg.desktop.model;

import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author Michal Adamczyk
 */
public class DocumentActivity {
    private ActivityType type;
    private Principal principal;
    private Date date;

    public ActivityType getType() {
        return type;
    }

    public void setType(ActivityType type) {
        this.type = type;
    }

    public Principal getPrincipal() {
        return principal;
    }

    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DocumentActivity)) {
            return false;
        }
        DocumentActivity that = (DocumentActivity) o;
        return type == that.type &&
                Objects.equals(principal, that.principal) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, principal, date);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("type", type)
                .append("principal", principal)
                .append("date", date)
                .toString();
    }
}
