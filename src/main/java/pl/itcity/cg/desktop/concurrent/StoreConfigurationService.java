package pl.itcity.cg.desktop.concurrent;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import pl.itcity.cg.desktop.configuration.ConfigManager;
import pl.itcity.cg.desktop.configuration.model.AppConfig;

/**
 * service handling configuration storage
 * @author Michal Adamczyk
 */
@Component
@Scope("prototype")
public class StoreConfigurationService extends Service<Void>{

    @Resource
    private ConfigManager configManager;

    /**
     * app config to be passed to task
     */
    private AppConfig appConfig;


    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {

            /**
             * app config for this task
             */
            private final AppConfig taskAppConfig = AppConfig.copyOf(appConfig);
            @Override
            protected Void call() throws Exception {
                String syncDirectory = taskAppConfig.getSyncDirectory();
                Path path = Paths.get(syncDirectory);
                if (!Files.exists(path)){
                    path.toFile().mkdirs();
                }
                configManager.storeConfig(taskAppConfig);
                return null;
            }
        };
    }

    public void setAppConfig(AppConfig appConfig) {
        this.appConfig = appConfig;
    }
}
