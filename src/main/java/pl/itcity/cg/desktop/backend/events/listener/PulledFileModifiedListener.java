package pl.itcity.cg.desktop.backend.events.listener;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import pl.itcity.cg.desktop.CgApplication;
import pl.itcity.cg.desktop.backend.files.events.PulledFileModifiedEvent;
import pl.itcity.cg.desktop.concurrent.PullFileService;
import pl.itcity.cg.desktop.concurrent.PushFileService;
import pl.itcity.cg.desktop.model.FileInfo;

import java.util.Optional;

/**
 * @author Patryk Majchrzycki
 */
@Component
public class PulledFileModifiedListener implements ApplicationListener<PulledFileModifiedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PulledFileModifiedListener.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(PulledFileModifiedEvent event) {
        PushFileService pushFileService = applicationContext.getBean(PushFileService.class,event.getPath(),event.getFileId(),event.getDicId());
        pushFileService.setOnSucceeded(e -> {
            ResponseEntity<FileInfo> response = pushFileService.getValue();
            if(HttpStatus.OK.equals(response.getStatusCode())){
                event.getPath().toFile().delete();
            }
        });
        pushFileService.start();
    }

}
