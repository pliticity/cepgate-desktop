package pl.itcity.cg.desktop;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.itcity.cg.desktop.controller.ConfigController;
import pl.itcity.cg.desktop.controller.DocumentListController;
import pl.itcity.cg.desktop.controller.LoginController;
import pl.itcity.cg.desktop.controller.common.ParentNodeAware;

public class CgApplication extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(CgApplication.class);
    private static final int DEFAULT_APPLICATION_WIDTH = 800;
    private static final int DEFAULT_APPLICATION_HEIGHT = 600;
    private static final String STYLES_STYLES_CSS = "/styles/styles.css";

    private ConfigurableApplicationContext context;
    private Stage mainStage;

    private boolean firstTime;
    /**
     * AWT tray icon
     */
    private TrayIcon trayIcon;

    private static CgApplication instance;

    BooleanProperty ready = new SimpleBooleanProperty(false);

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
        mainStage.show();
    }

    /**
     * creates tray icon
     */
    public void createTrayIcon(){
        if (SystemTray.isSupported()){
            LOGGER.info("system tray supported, initializing system tray");
            SystemTray systemTray = SystemTray.getSystemTray();
            mainStage.setOnCloseRequest(t -> hide());

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
            trayIcon = new TrayIcon(image, "Title", popup);
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

    private void showProgramIsMinimizedMsg() {
        if (firstTime) {
            trayIcon.displayMessage("Some message.",
                                    "Some other message.",
                                    TrayIcon.MessageType.INFO);
            firstTime = false;
        }
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

    public Stage getMainStage() {
        return mainStage;
    }
}
