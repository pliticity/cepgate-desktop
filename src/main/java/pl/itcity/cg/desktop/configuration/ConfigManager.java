package pl.itcity.cg.desktop.configuration;

import pl.itcity.cg.desktop.configuration.exception.ConfigManagerException;
import pl.itcity.cg.desktop.configuration.model.AppConfig;

/**
 * Interface enabling app configuration
 *
 * @author Michal Adamczyk
 */
public interface ConfigManager {

    /**
     * gets current app config
     *
     * @return app config
     */
    AppConfig getAppConfig();

    /**
     * stores given config
     *
     * @param config
     *         config
     * @throws ConfigManagerException
     */
    void storeConfig(AppConfig config) throws ConfigManagerException;

    /**
     * gets application name.
     * @return application name
     */
    default String getAppName() {
        return "cepgate";
    }

    /**
     * gets config resource name. Default implementation corresponds to config file
     * @return config resource name
     */
    default String getConfigResourceName() {
        return "config.json";
    }
}
