package pl.itcity.cg.desktop.configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import pl.itcity.cg.desktop.configuration.exception.ConfigManagerException;
import pl.itcity.cg.desktop.configuration.model.AppConfig;

/**
 * Simple config manager implementation basing on filesystem storage
 *
 * @author Michal Adamczyk
 */
@Component
public class FilesysteConfigManager implements ConfigManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilesysteConfigManager.class);

    @Override
    public AppConfig getAppConfig() {
        Path configResourcePath = getUserHomePathPath()
                .resolve("." + getAppName())
                .resolve(getConfigResourceName());
        File configResourceFile = new File(configResourcePath.toAbsolutePath()
                                     .toString());
        if (configResourceFile.exists()){
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.readValue(configResourceFile, AppConfig.class);
            } catch (IOException e) {
                LOGGER.error("unable to read serialized config: ", e);
                return getDefaultAppConfig();
            }
        } else {
            ensureDirectoriesExist(configResourcePath);
            AppConfig defaultAppConfig = getDefaultAppConfig();
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                objectMapper.writeValue(configResourceFile, defaultAppConfig);
            } catch (IOException e) {
                LOGGER.error("unable to store serialized config: ", e);
            }
            return defaultAppConfig;
        }
    }

    private void ensureDirectoriesExist(Path configResourcePath) {
        Path parent = configResourcePath.getParent();
        if (!Files.exists(parent)) {
            parent.toFile().mkdirs();
        }
    }

    /**
     * gets default app config
     *
     * @return default app config
     */
    private AppConfig getDefaultAppConfig() {
        AppConfig appConfig = new AppConfig();
        Path appDefaultPath = getUserHomePathPath().resolve(getAppName());
        appConfig.setSyncDirectory(appDefaultPath.toString());
        return appConfig;
    }

    private Path getUserHomePathPath() {
        return FileSystems.getDefault()
                .getPath(System.getProperty("user.home"));
    }

    @Override
    public void storeConfig(AppConfig config) throws ConfigManagerException {
        ObjectMapper objectMapper = new ObjectMapper();
        Path configResourcePath = getUserHomePathPath().resolve("." + getAppName())
                .resolve(getConfigResourceName());
        File configResourceFile = new File(configResourcePath.toAbsolutePath()
                                                   .toString());
        configResourcePath.getParent().toFile().mkdirs();
        try {
            if (!configResourceFile.exists()){
                configResourceFile.createNewFile();
            }
            objectMapper.writeValue(configResourceFile, config);
        } catch (IOException e) {
            throw new ConfigManagerException("unable to write congfig", e);
        }
    }
}
