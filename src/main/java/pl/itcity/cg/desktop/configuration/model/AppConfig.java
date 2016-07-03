package pl.itcity.cg.desktop.configuration.model;

/**
 * Model for local configuration
 *
 * @author Michal Adamczyk
 */
public class AppConfig {

    /**
     * directory to be synchronized with cepgate webapp
     */
    private String syncDirectory;

    public String getSyncDirectory() {
        return syncDirectory;
    }

    public void setSyncDirectory(String syncDirectory) {
        this.syncDirectory = syncDirectory;
    }

    /**
     * creates copy of other instance
     *
     * @param other
     *         other instance
     * @return copy of other
     */
    public static AppConfig copyOf(AppConfig other) {
        AppConfig appConfig = new AppConfig();
        appConfig.setSyncDirectory(other.syncDirectory);
        return appConfig;
    }
}
