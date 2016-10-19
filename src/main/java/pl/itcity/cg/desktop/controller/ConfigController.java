package pl.itcity.cg.desktop.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import pl.itcity.cg.desktop.CgApplication;
import pl.itcity.cg.desktop.backend.files.DocumentSynchronizer;
import pl.itcity.cg.desktop.concurrent.DocumentListService;
import pl.itcity.cg.desktop.concurrent.DocumentSynchronizingService;
import pl.itcity.cg.desktop.concurrent.GetConfigurationService;
import pl.itcity.cg.desktop.concurrent.StoreConfigurationService;
import pl.itcity.cg.desktop.configuration.model.AppConfig;
import pl.itcity.cg.desktop.controller.common.ParentNodeAware;
import pl.itcity.cg.desktop.integration.service.JMSService;
import pl.itcity.cg.desktop.integration.service.TokenService;
import pl.itcity.cg.desktop.model.DocumentInfo;
import pl.itcity.cg.desktop.user.UserContext;

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

    @FXML
    private ProgressBar synchronizationProgressBar;

    @Resource
    private StoreConfigurationService storeConfigurationService;

    @Resource
    private DocumentSynchronizingService documentSynchronizingService;

    @Resource
    private DocumentListService documentListService;

    @Resource
    private GetConfigurationService getConfigurationService;

    @Resource
    private DocumentSynchronizer documentSynchronizer;

    private final DirectoryChooser directoryChooser = initDirectoryChooser();

    @Override
    public Parent getView() {
        return configView;
    }

    @Resource
    private TokenService tokenService;

    @Resource
    private JMSService jmsService;

    @Resource
    private UserContext userContext;

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
                    initIntegration(syncDirectoryValue);
                }
            } else {
                LOGGER.debug("storeConfigurationService already running");
            }
        });
        synchronizationProgressBar.setVisible(false);
        synchronizationProgressBar.progressProperty().bind(documentSynchronizingService.progressProperty());
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
            /*CgApplication.getInstance()
                    .goToDocumentList();*/
            fetchDocumentsAndSynchronize();
        });
        storeConfigurationService.setOnFailed(event1 -> {
            Throwable exception = storeConfigurationService.getException();
            LOGGER.error("exception while running storeConfigurationService: ", exception);
            errorLabel.setText(getMessage("config.error.serviceException", new Object[]{exception.getMessage()}));
        });
        storeConfigurationService.restart();
    }

    private void fetchDocumentsAndSynchronize() {
        if (documentListService.isRunning()){
            LOGGER.warn("documentListService already running");
        } else {
            documentListService.setOnFailed(event -> {
                Throwable exception = documentListService.getException();
                LOGGER.error("exception while feching documents:",exception);
                errorLabel.setText(getMessage("document.list.error", new Object[]{exception.getMessage()}));
            });
            documentListService.setOnSucceeded(event -> {
                errorLabel.setText(getMessage("document.list.succes.starting.sync"));
                List<DocumentInfo> value = documentListService.getValue();
                if (documentSynchronizingService.isRunning()){
                    LOGGER.warn("DocumentSynchronizingService already running");
                } else {
                    documentSynchronizingService.setOnFailed(event1 -> handleSynchronizationFalied());
                    documentSynchronizingService.setOnSucceeded(event1 -> {
                        errorLabel.setText(getMessage("document.synchronization.success"));
                        registerWatchers();
                        CgApplication.getInstance().sendToTray();
                    });
                    documentSynchronizingService.setDocuments(value);
                    documentSynchronizingService.restart();
                    synchronizationProgressBar.setVisible(true);
                }
            });
            documentListService.restart();
        }
    }

    /**
     * registers document change watchers
     *
     * consider replacing direct synchronizer call with service
     */
    private void registerWatchers() {
        try {
            documentSynchronizer.registerDocumentChangeWatchers();
            LOGGER.info("watchers registered");
        } catch (IOException e) {
            LOGGER.error("exception while registering watchers", e);
            errorLabel.setText(getMessage("document.synchronization.watchers.error", new Object[]{e.getMessage()}));
        }
    }

    /**
     * handles document synchronization service failure
     */
    private void handleSynchronizationFalied() {
        Throwable exception = documentSynchronizingService.getException();
        LOGGER.error("exception while synchronizing documents:", exception);
        errorLabel.setText(getMessage("document.synchronization.error", new Object[]{exception.getMessage()}));
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

    private void initIntegration(String syncDirectoryValue){
        Path checkInPath = Paths.get(syncDirectoryValue,"/checkIn");
        if(Files.notExists(checkInPath)){
            checkInPath.toFile().mkdirs();
        }
        String email = userContext.getContext().getUser();
        String desktopToken = tokenService.generateToken(email);
        jmsService.initChannel(desktopToken);
        jmsService.connect(desktopToken);
        tokenService.registerToken(desktopToken);
    }
}
