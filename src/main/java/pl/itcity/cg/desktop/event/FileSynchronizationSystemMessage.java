package pl.itcity.cg.desktop.event;

import java.nio.file.Path;

/**
 * Event for file synchronization result
 *
 * @author Michal Adamczyk
 */
public class FileSynchronizationSystemMessage extends SystemMessage {

    private final Path file;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source
     *         the object on which the event initially occurred (never {@code null})
     * @param eventResultType
     *         event result type
     * @param file
     *         file
     */
    public FileSynchronizationSystemMessage(Object source, EventResultType eventResultType, Path file) {
        super(source, eventResultType);
        this.file = file;
    }

    public Path getFile() {
        return file;
    }
}
