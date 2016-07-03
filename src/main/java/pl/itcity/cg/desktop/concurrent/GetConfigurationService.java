package pl.itcity.cg.desktop.concurrent;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import pl.itcity.cg.desktop.configuration.ConfigManager;
import pl.itcity.cg.desktop.configuration.model.AppConfig;

/**
 * Service getting configuration
 *
 * @author Michal Adamczyk
 */
@Component
@Scope("prototype")
public class GetConfigurationService extends Service<AppConfig>{

    @Resource
    private ConfigManager configManager;

    @Override
    protected Task<AppConfig> createTask() {
        return new Task<AppConfig>() {
            @Override
            protected AppConfig call() throws Exception {
                return configManager.getAppConfig();
            }
        };
    }
}
