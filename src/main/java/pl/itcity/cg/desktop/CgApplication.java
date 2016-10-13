package pl.itcity.cg.desktop;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pl.itcity.cg.desktop.controller.ActionConfirmController;
import pl.itcity.cg.desktop.controller.ConfigController;
import pl.itcity.cg.desktop.controller.DocumentListController;
import pl.itcity.cg.desktop.controller.LoginController;
import pl.itcity.cg.desktop.controller.common.ParentNodeAware;
import pl.itcity.cg.desktop.integration.service.TokenService;

public class CgApplication extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(CgApplication.class);
    private static final int DEFAULT_APPLICATION_WIDTH = 800;
    private static final int DEFAULT_APPLICATION_HEIGHT = 300;
    private static final String STYLES_STYLES_CSS = "/styles/styles.css";

    private static final javafx.scene.image.Image APP_ICON = new javafx.scene.image.Image(CgApplication.class.getClassLoader().getResourceAsStream
            ("images/cuberix-logo.png"));

    private ConfigurableApplicationContext context;
    /**
     * main stage reference
     */
    private Stage mainStage;

    /**
     * popup stage reference
     */
    private Stage popupStage;

    private boolean firstTime;

    /**
     * AWT tray icon
     */
    private TrayIcon trayIcon;

    private static CgApplication instance;
    BooleanProperty ready = new SimpleBooleanProperty(false);
    private MessageSource messageSource;

    public CgApplication() {
        instance = this;
    }

    public static CgApplication getInstance(){
        return instance;
    }

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    public void start(final Stage stage) throws Exception {

        LOGGER.info("Starting CepGate JavaFX application");
        context = new AnnotationConfigApplicationContext(AppBeanFactory.class);
        messageSource = context.getBean(MessageSource.class);
        ready.addListener((ov, t, t1) -> {
            if (Boolean.TRUE.equals(t1)) {
                //the runnable below will be run on MAIN THREAD - the only one that can touch UI
                Platform.runLater(stage::show);
            }
        });

        mainStage = stage;
        mainStage.setMinWidth(DEFAULT_APPLICATION_WIDTH);
        mainStage.setMinHeight(DEFAULT_APPLICATION_HEIGHT);
        createTrayIcon();
        firstTime = true;
        Platform.setImplicitExit(false);
        LoginController loginController = context.getBean(LoginController.class);
        gotoControllerView(loginController, true);
        mainStage.setOnCloseRequest(event -> {
            if (SystemTray.isSupported()) {
                ActionConfirmController actionConfirmController = context.getBean(ActionConfirmController.class);
                actionConfirmController.setAfterConfirmExecutor(this::doCloseApp);
                actionConfirmController.setConfirmButtonLabel(getMessage("alert.button.exit"));
                actionConfirmController.setAfterNotConfirmExecutor(this::sendToTray);
                actionConfirmController.setNotConfirmButtonLabel(getMessage("alert.button.minimize"));
                actionConfirmController.updateDetailsValue(getMessage("alert.confirm.exit"));
                showPopup(actionConfirmController.getView(),getMessage("alert.confitm.exit.title"));
            } else {
                doCloseApp();
            }
            event.consume();
        });
        //mainStage.show();
    }

    private void doCloseApp() {
        TokenService tokenService = context.getBean(TokenService.class);
        tokenService.registerToken(null);
        context.close();
        System.exit(0);
    }

    /**
     * closes popup view
     */
    public void closePopup() {
        popupStage.getScene()
                .setRoot(new VBox());
        popupStage.close();
    }


    /**
     * shows popup
     *
     * @param view
     *         view
     * @param title
     *         title
     */
    public void showPopup(final Parent view, String title) {
        LOGGER.debug("Showing new popup");
        Scene scene = new Scene(view);
        ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.add(STYLES_STYLES_CSS);
        popupStage = new Stage(StageStyle.DECORATED);
        popupStage.getIcons()
                .add(APP_ICON);
        popupStage.setScene(scene);
        popupStage.initModality(Modality.WINDOW_MODAL);
        popupStage.initOwner(mainStage.getScene() != null ? mainStage.getScene()
                .getWindow() : null);
        if (StringUtils.isBlank(title)) {
            title = "cepgate";
        }
        popupStage.setTitle(title);
        popupStage.setOnCloseRequest(event -> closePopup());
        popupStage.show();
    }

    /**
     * creates tray icon
     */
    public void createTrayIcon(){
        if (SystemTray.isSupported()){
            LOGGER.info("system tray supported, initializing system tray");
            SystemTray systemTray = SystemTray.getSystemTray();

            ActionListener showListener = e -> Platform.runLater(mainStage::show);

            // load an image
            java.awt.Image image = null;
            try {
                image = javax.imageio.ImageIO.read(this.getClass().getResourceAsStream("/images/cuberix-logo.png"));
            } catch (IOException ex) {
                LOGGER.error("unable to load tray image", ex);
            }
            // create a popup menu
            PopupMenu popup = new PopupMenu();

            MenuItem showItem = new MenuItem("Show");
            showItem.addActionListener(showListener);
            popup.add(showItem);

            MenuItem closeItem = new MenuItem("Close");
            closeItem.addActionListener(e -> System.exit(0));
            popup.add(closeItem);
            /// ... add other items
            // construct a TrayIcon
            trayIcon = new TrayIcon(image, "Cepgate", popup);
            trayIcon.setImageAutoSize(true);
            // set the TrayIcon properties
            trayIcon.addActionListener(showListener);
            // ...
            // add the tray image
            try {
                systemTray.add(trayIcon);
            } catch (AWTException e) {
                LOGGER.error("unable to add tray icon:", e);
            }
        } else {
            LOGGER.warn("system tray not supported!!");
        }
    }

    /**
     * shows "program minimized" message in tray
     */
    private void showProgramIsMinimizedMsg() {
        if (firstTime) {
            String code = "application.tray.application.minimized";
            String appMinimizedMessage = getMessage(code);
            trayIcon.displayMessage(appMinimizedMessage, getMessage("application.tray.description"), TrayIcon.MessageType.INFO);
            firstTime = false;
        }
    }

    /**
     * displays message on tray
     *
     * @param caption
     *         caption
     * @param text
     *         text
     * @param messageType
     *         message type
     */
    public void showTrayMessage(String caption, String text, TrayIcon.MessageType messageType) {
        Platform.runLater(() -> {
            if (trayIcon != null) {
                trayIcon.displayMessage(caption, text, messageType);
            } else {
                LOGGER.warn("trayIcon is not initialized");
            }
        });
    }

    /**
     * gets message for given code
     *
     * @param code
     *         code, not null
     * @return message or code if no message is defined for given code
     */
    private String getMessage(String code) {
        return messageSource.getMessage(code, new Object[]{}, code, Locale.getDefault());
    }

    /**
     * hides main stage or closes app if system tray is not supported
     */
    private void hide() {
        Platform.runLater(() -> {
            if (SystemTray.isSupported()) {
                mainStage.hide();
                showProgramIsMinimizedMsg();
            } else {
                System.exit(0);
            }
        });
    }

    /**
     * sends app to tray if tray is supported
     */
    public void sendToTray() {
        if (SystemTray.isSupported()) {
            LOGGER.debug("System tray supported - hiding main stage of app");
            hide();
        } else {
            LOGGER.debug("System tray not supported, ignoring sendToTray call");
        }
    }

    /**
     * navigates to document list view
     */
    public void goToDocumentList() {
        DocumentListController documentListController = context.getBean(DocumentListController.class);
        gotoControllerView(documentListController, false);
        documentListController.fetchDocuments();
    }

    /**
     * navigates to config view
     */
    public void goToConfig(){
        ConfigController configController = context.getBean(ConfigController.class);
        gotoControllerView(configController,false);
        configController.fetchConfig();
    }

    private void gotoControllerView(ParentNodeAware controller, boolean forceDefaultSize) {
        Parent rootNode = controller.getView();
        replaceSceeneContent(rootNode, forceDefaultSize);
        ready.setValue(Boolean.TRUE);
    }

    private void replaceSceeneContent(Parent rootNode, boolean forceDefaultSize) {
        LOGGER.debug("Showing JFX scene");
        Scene scene = mainStage.getScene();
        if (scene == null) {
            scene = forceDefaultSize ? new Scene(rootNode, DEFAULT_APPLICATION_WIDTH,
                                                 DEFAULT_APPLICATION_HEIGHT) : new Scene(rootNode);
            ObservableList<String> stylesheets = scene.getStylesheets();
            stylesheets.add(STYLES_STYLES_CSS);
            mainStage.setTitle("CG application");
            mainStage.setScene(scene);
        } else {
            scene.setRoot(rootNode);
        }
    }

    public TrayIcon getTrayIcon() {
        return trayIcon;
    }

    public Stage getMainStage() {
        return mainStage;
    }
}
