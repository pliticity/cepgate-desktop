package pl.itcity.cg.desktop.notification;

import java.awt.*;
import java.nio.file.Path;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import pl.itcity.cg.desktop.CgApplication;
import pl.itcity.cg.desktop.event.EventResultType;
import pl.itcity.cg.desktop.event.FileSynchronizationSystemMessage;
import pl.itcity.cg.desktop.event.SystemMessage;

/**
 * System messages listener
 *
 * @author Michal Adamczyk
 */
@Component
public class SystemMessageListener implements ApplicationListener<SystemMessage>{

    @Resource
    private MessageSource messageSource;

    private static final Map<EventResultType,TrayIcon.MessageType> EVENT_TYPE_MAPPING = prepareEventMapping();
    @Override
    public void onApplicationEvent(SystemMessage event) {
        EventResultType eventResultType = event.getEventResultType();
        //[michal.adamczyk] tray icon operations does not need to be run in main thread, but it's the easiest way to force one message display at a time.
        Platform.runLater(() -> {
            TrayIcon trayIcon = CgApplication.getInstance()
                    .getTrayIcon();
            String simpleName = event.getClass()
                    .getSimpleName();
            String caption = messageSource.getMessage(simpleName, new Object[]{}, simpleName, Locale.getDefault());
            String message = getText(event);
            trayIcon.displayMessage(caption, message, EVENT_TYPE_MAPPING.get(eventResultType));
        });

    }

    /**
     * gets text for given event
     *
     * @param event
     *         event, not null
     * @return text for given event
     */
    private String getText(SystemMessage event) {
        String message = null;
        if (event instanceof FileSynchronizationSystemMessage) {
            FileSynchronizationSystemMessage fileSyncEvent = (FileSynchronizationSystemMessage) event;
            Path file = fileSyncEvent.getFile();
            String eventTextCode = fileSyncEvent.getClass()
                    .getSimpleName()
                    .concat(".")
                    .concat(event.getEventResultType().name())
                    .concat(".")
                    .concat("text");
            message = messageSource.getMessage(eventTextCode, new Object[]{file}, eventTextCode, Locale.getDefault());
        }
        if (StringUtils.isEmpty(message)) {
            String defaultTextCode = SystemMessage.class.getSimpleName()
                    .concat(".")
                    .concat("text");
            message = messageSource.getMessage(defaultTextCode, new Object[]{}, defaultTextCode, Locale.getDefault());
        }
        return message;
    }

    private static Map<EventResultType, TrayIcon.MessageType> prepareEventMapping() {
        Map<EventResultType, TrayIcon.MessageType> mapping = new EnumMap<>(EventResultType.class);
        mapping.put(EventResultType.ERROR, TrayIcon.MessageType.ERROR);
        mapping.put(EventResultType.INFO, TrayIcon.MessageType.INFO);
        mapping.put(EventResultType.SUCCESS, TrayIcon.MessageType.INFO);
        mapping.put(EventResultType.WARNING, TrayIcon.MessageType.WARNING);
        return Collections.unmodifiableMap(mapping);
    }
}
