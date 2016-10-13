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

    private String fileId;

    public FileWatcher(String fileName, Path path, String dicId, String fileId) {
        this.fileName = fileName;
        this.path = path;
        this.dicId = dicId;
        this.fileId = fileId;
    }

    @Override
    public void run() {
        try (WatchService watchService = FileSystems.getDefault()
                .newWatchService()) {
            path.register(watchService, ENTRY_MODIFY);
            while (true) {
                WatchKey watchKey = watchService.take();
                for (WatchEvent<?> event : watchKey.pollEvents()) {
                    Path modified = (Path) event.context();
                    if (modified.endsWith(this.fileName)) {
                        applicationEventPublisher.publishEvent(new PulledFileModifiedEvent(event, Paths.get(path.toString(),fileName), this.dicId,this.fileId));
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
