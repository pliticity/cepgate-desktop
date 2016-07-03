package pl.itcity.cg.desktop.controller;

import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import pl.itcity.cg.desktop.CgApplication;
import pl.itcity.cg.desktop.concurrent.GetConfigurationService;
import pl.itcity.cg.desktop.concurrent.StoreConfigurationService;
import pl.itcity.cg.desktop.configuration.model.AppConfig;
import pl.itcity.cg.desktop.controller.common.ParentNodeAware;

/**
 * @author Michal Adamczyk controller for configuration screen
 */
public class ConfigController extends BaseController implements ParentNodeAware{

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigController.class);

    @FXML
    private Parent configView;

    @FXML
    private Button browseSyncDirButton;

    @FXML
    private Button saveButton;

    @FXML
    private TextField syncDirectory;

    @FXML
    private Label errorLabel;

    @Resource
    private StoreConfigurationService storeConfigurationService;

    @Resource
    private GetConfigurationService getConfigurationService;

    private final DirectoryChooser directoryChooser = initDirectoryChooser();

    @Override
    public Parent getView() {
        return configView;
    }

    @PostConstruct
    private void init(){
        browseSyncDirButton.setOnAction(event -> {
            File file = directoryChooser.showDialog(CgApplication.getInstance()
                                                            .getMainStage());
            Optional.ofNullable(file).ifPresent(result -> {
                String path = result.getPath();
                syncDirectory.setText(path);
                errorLabel.setText(StringUtils.EMPTY);
            });

        });
        saveButton.setOnAction(event -> {
            LOGGER.info("save butto clicked");
            if (!storeConfigurationService.isRunning()) {
                saveButton.setDisable(true);
                String syncDirectoryValue = this.syncDirectory.getText();
                if (StringUtils.isBlank(syncDirectoryValue)){
                    errorLabel.setText(getMessage("config.error.emptySyncDir"));
                    saveButton.setDisable(false);
                } else {
                    configureAndRestartStoreService(syncDirectoryValue);
                }
            } else {
                LOGGER.debug("storeConfigurationService already running");
            }
        });
    }

    /**
     * fetches stored config
     */
    public void fetchConfig(){
        setControlsDisabled(true);
        if (getConfigurationService.isRunning()){
            LOGGER.debug("getConfigurationService already running");
        } else {
            getConfigurationService.setOnSucceeded(event -> {
                AppConfig value = getConfigurationService.getValue();
                String syncDirectoryValue = value.getSyncDirectory();
                this.syncDirectory.setText(
                        StringUtils.isNotBlank(syncDirectoryValue) ? syncDirectoryValue : StringUtils.EMPTY);
                setControlsDisabled(false);
            });
            getConfigurationService.setOnFailed(event -> {
                setControlsDisabled(false);
                errorLabel.setText(getMessage("config.error.unableToFetch"));
            });
            getConfigurationService.restart();
        }
    }

    /**
     * sets disabled flag on controls
     *
     * @param value
     *         value to set
     */
    private void setControlsDisabled(boolean value) {
        saveButton.setDisable(value);
        browseSyncDirButton.setDisable(value);
    }

    /**
     * configures and restarts store service
     *
     * @param syncDirectoryValue
     *         sync directory value
     */
    private void configureAndRestartStoreService(String syncDirectoryValue) {
        AppConfig appConfig = new AppConfig();
        appConfig.setSyncDirectory(syncDirectoryValue);
        storeConfigurationService.setAppConfig(appConfig);
        storeConfigurationService.setOnSucceeded(event1 -> {
            errorLabel.setText(getMessage("config.sava.success"));
            CgApplication.getInstance()
                    .goToDocumentList();
        });
        storeConfigurationService.setOnFailed(event1 -> {
            Throwable exception = storeConfigurationService.getException();
            LOGGER.error("exception while running storeConfigurationService: ", exception);
            errorLabel.setText(getMessage("config.error.serviceException", new Object[]{exception.getMessage()}));
        });
        storeConfigurationService.restart();
    }

    /**
     * inits directory chooser
     *
     * @return initialized directory chooser
     */
    private static DirectoryChooser initDirectoryChooser() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        String property = System.getProperty("user.home");
        directoryChooser.setInitialDirectory(Paths.get(property)
                                                     .toFile());
        return directoryChooser;
    }
}
