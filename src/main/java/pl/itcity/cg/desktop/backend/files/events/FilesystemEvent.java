package pl.itcity.cg.desktop.backend.files.events;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.context.ApplicationEvent;

/**
 * Filesystem event model
 *
 * @author Michal Adamczyk
 */
public class FilesystemEvent extends ApplicationEvent{

    private final Path path;

    private final WatchEvent.Kind kind;

    private final Date eventDate = new Date();

    /**
     * constructor initializing necessary fields
     *
     * @param source
     *         event source
     * @param path
     *         path
     * @param kind
     *         watch event kind
     */
    public FilesystemEvent(Object source, Path path, WatchEvent.Kind kind) {
        super(source);
        this.path = path;
        this.kind = kind;
    }

    public Path getPath() {
        return path;
    }

    public WatchEvent.Kind getKind() {
        return kind;
    }

    public Date getEventDate() {
        return eventDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FilesystemEvent)) {
            return false;
        }
        FilesystemEvent that = (FilesystemEvent) o;
        return Objects.equals(path, that.path) &&
                Objects.equals(kind, that.kind) &&
                Objects.equals(eventDate, that.eventDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, kind, eventDate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("path", path)
                .append("kind", kind)
                .append("eventDate", eventDate)
                .toString();
    }
}
