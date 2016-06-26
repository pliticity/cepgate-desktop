package pl.itcity.cg.desktop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
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

        LOGGER.info("Starting Hello JavaFX and Maven demonstration application");
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
        LoginController loginController = context.getBean(LoginController.class);
        gotoControllerView(loginController, true);
        mainStage.show();
    }

    /**
     * navigates to document list view
     */
    public void goToDocumentList() {
        DocumentListController documentListController = context.getBean(DocumentListController.class);
        gotoControllerView(documentListController, false);
        documentListController.fetchDocuments();
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
}
