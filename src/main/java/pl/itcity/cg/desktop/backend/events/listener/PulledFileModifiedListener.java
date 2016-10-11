package pl.itcity.cg.desktop.backend.events.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import pl.itcity.cg.desktop.backend.files.events.PulledFileModifiedEvent;

/**
 * @author Patryk Majchrzycki
 */
@Component
public class PulledFileModifiedListener implements ApplicationListener<PulledFileModifiedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PulledFileModifiedListener.class);

    @Override
    public void onApplicationEvent(PulledFileModifiedEvent pulledFileModifiedEvent) {
        LOGGER.info("event "+pulledFileModifiedEvent);
    }

}
