package pl.itcity.cg.desktop.event;

import java.util.Date;

import org.springframework.context.ApplicationEvent;

/**
 * System message event
 *
 * @author Michal Adamczyk
 */
public class SystemMessage extends ApplicationEvent{

    private final Date date;

    private final EventResultType eventResultType;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source
     *         the object on which the event initially occurred (never {@code null})
     * @param eventResultType event result type
     */
    public SystemMessage(Object source, EventResultType eventResultType) {
        super(source);
        this.eventResultType = eventResultType;
        date = new Date();
    }

    public Date getDate() {
        return date;
    }

    public EventResultType getEventResultType() {
        return eventResultType;
    }
}
