package pl.itcity.cg.desktop.backend.files;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import pl.itcity.cg.desktop.backend.files.events.DocumentFilesystemEventsFactory;
import pl.itcity.cg.desktop.backend.files.runnables.DirectoryWather;
import pl.itcity.cg.desktop.configuration.ConfigManager;
import pl.itcity.cg.desktop.configuration.model.AppConfig;

/**
 * default implementation of document synchronizer
 *
 * @author Michal Adamczyk
 */
@Component
public class DefaultDocumentSynchronizer implements DocumentSynchronizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDocumentSynchronizer.class);

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Resource
    private ConfigManager configManager;

    /**
     * registers document change watchers for given directory
     *
     */
    @Override
    public void registerDocumentChangeWatchers() throws IOException {
        LOGGER.info("about to register directory watchers");
        Date start = new Date();
        AppConfig appConfig = configManager.getAppConfig();
        String syncDirectory = appConfig.getSyncDirectory();
        HashSet<Path> directoriesToWatch = new HashSet<>();
        Files.walkFileTree(Paths.get(syncDirectory), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException{
                if (FileConstants.META_EXTENSTION.equals(FilenameUtils.getExtension(file.toString()))) {
                    Path pathToWatch = file.getParent()
                            .toAbsolutePath();
                    LOGGER.debug("path " + pathToWatch + " schould be watched");
                    directoriesToWatch.add(pathToWatch);
                }
                return FileVisitResult.CONTINUE;
            }
        });
        LOGGER.info("Found " + directoriesToWatch.size() + " directories to watch");
        ExecutorService executorService = Executors.newFixedThreadPool(directoriesToWatch.size());
        directoriesToWatch.forEach(path -> executorService.execute(new DirectoryWather(applicationEventPublisher, new DocumentFilesystemEventsFactory(path), path)));
        Date end = new Date();
        long duration = end.getTime() - start.getTime();
        LOGGER.info("directory watcher registered for root directory: "+syncDirectory+ ", duration = "+duration+"ms");
    }
}
