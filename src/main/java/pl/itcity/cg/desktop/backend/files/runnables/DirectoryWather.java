package pl.itcity.cg.desktop.backend.files.runnables;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

import com.google.common.base.Preconditions;

import pl.itcity.cg.desktop.backend.files.events.FilesystemEvent;
import pl.itcity.cg.desktop.backend.files.events.FilesystemEventFactory;

/**
 * Directory watcher handling filesystem events
 *
 * @author Michal Adamczyk
 */
public class DirectoryWather implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirectoryWather.class);

    /**
     * event publisher
     */
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * filesystem events factory
     */
    private final FilesystemEventFactory filesystemEventFactory;

    /**
     * patchToWatch path for watcher
     */
    private final Path patchToWatch;

    /**
     * constructor initializing necessary fields
     *
     * @param applicationEventPublisher
     *         application event publisher, not null
     * @param filesystemEventFactory
     *         events factory
     * @param patchToWatch
     *         directory to watch
     */
    public DirectoryWather(ApplicationEventPublisher applicationEventPublisher, FilesystemEventFactory filesystemEventFactory, Path patchToWatch) {
        Preconditions.checkArgument(applicationEventPublisher != null, "applicationEventPublisher can not be null");
        Preconditions.checkArgument(filesystemEventFactory != null, "filesystemEventFactory can not be null");
        Preconditions.checkArgument(patchToWatch != null, "patchToWatch can not be null");
        this.filesystemEventFactory = filesystemEventFactory;
        this.applicationEventPublisher = applicationEventPublisher;
        this.patchToWatch = patchToWatch;
    }

    @Override
    public void run() {
        try {
            LOGGER.info("about to register watcher for path: " + patchToWatch);
            WatchService watchService = FileSystems.getDefault()
                    .newWatchService();
            patchToWatch.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY, OVERFLOW);
            while (true){
                WatchKey watchKey;
                try {
                    watchKey = watchService.take();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                for (WatchEvent<?> watchEvent : watchKey.pollEvents()) {
                    publishApplicationEvent(watchEvent);
                }
                watchKey.reset();
            }
            LOGGER.debug("shutting down directoryWatcher thread");
        } catch (IOException e) {
            LOGGER.error("", e);
            Thread.currentThread().interrupt();
        }
    }

    private void publishApplicationEvent(WatchEvent<?> event) {
        WatchEvent.Kind<?> kind = event.kind();
        if (kind.equals(OVERFLOW)) {
            LOGGER.warn("overflow watch event");
        } else {
            Path path = (Path) event.context();
            LOGGER.debug("filesystem event intercepted "+kind + ": " + path);
            if (filesystemEventFactory.acceptPath(path)) {
                FilesystemEvent filesystemEvent = filesystemEventFactory.filesystemEvent(this, path, kind);
                LOGGER.info("path accepted: " + path + " , about to publish event: " + filesystemEvent);
                applicationEventPublisher.publishEvent(filesystemEvent);
            } else {
                LOGGER.info("path not accepted: " + path.toAbsolutePath());
            }
        }
    }
}
