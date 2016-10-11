package pl.itcity.cg.desktop.backend.files.runnables;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.itcity.cg.desktop.backend.files.events.PulledFileModifiedEvent;

import java.io.IOException;
import java.nio.file.*;
import java.text.MessageFormat;

import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

/**
 * @author Patryk Majchrzycki
 */
@Component
@Scope("prototype")
public class FileWatcher implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileWatcher.class);

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    private String fileName;

    private Path path;

    private String dicId;

    public FileWatcher(String fileName, Path path,String dicId) {
        this.fileName = fileName;
        this.path = path;
        this.dicId = dicId;
    }

    @Override
    public void run() {
        try (WatchService watchService = FileSystems.getDefault()
                .newWatchService()) {
            path.register(watchService, ENTRY_MODIFY, ENTRY_DELETE);
            while (true) {
                WatchKey watchKey = watchService.take();
                for (WatchEvent<?> event : watchKey.pollEvents()) {
                    if (ENTRY_DELETE.equals(event.kind())) {LOGGER.info(MessageFormat.format("File {0} was deleted", path.toString()));
                        Thread.currentThread().interrupt();
                    } else {
                        Path modified = (Path) event.context();
                        if (modified.endsWith(this.fileName)) {
                            LOGGER.info(MessageFormat.format("File {0} was modified", path.toString()));
                            applicationEventPublisher.publishEvent(new PulledFileModifiedEvent(event,modified,this.dicId));
                        }
                    }
                }
                watchKey.reset();
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }

    }


}
