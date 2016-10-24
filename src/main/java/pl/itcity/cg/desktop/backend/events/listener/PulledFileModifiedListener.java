package pl.itcity.cg.desktop.backend.events.listener;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import pl.itcity.cg.desktop.backend.files.events.PulledFileModifiedEvent;
import pl.itcity.cg.desktop.concurrent.PushFileService;
import pl.itcity.cg.desktop.model.FileInfo;

import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @author Patryk Majchrzycki
 */
@Component
public class PulledFileModifiedListener implements ApplicationListener<PulledFileModifiedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PulledFileModifiedListener.class);

    private static final String TITLE_BUNDLE = "pulled.file.upload.title";
    private static final String CONTENT_BUNDLE = "pulled.file.upload.content";
    private static final String YES_BUNDLE = "pulled.file.upload.yes";
    private static final String NO_BUNDLE = "pulled.file.upload.no";

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MessageSource messageSource;

    private boolean showing;

    @Override
    public void onApplicationEvent(PulledFileModifiedEvent event) {
        if (!showing) {
            showing = true;
            FutureTask<Void> futureTask = new FutureTask<Void>(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle(resolveMessage(TITLE_BUNDLE));
                    alert.setHeaderText(resolveMessage(CONTENT_BUNDLE, event.getPath().getFileName().toString()));
                    alert.getButtonTypes().clear();
                    ButtonType yes = new ButtonType(resolveMessage(YES_BUNDLE),ButtonBar.ButtonData.OK_DONE);
                    ButtonType no = new ButtonType(resolveMessage(NO_BUNDLE), ButtonBar.ButtonData.CANCEL_CLOSE);
                    alert.getButtonTypes().addAll(yes,no);
                    Optional<ButtonType> option = alert.showAndWait();
                    if (ButtonBar.ButtonData.OK_DONE.equals(option.get().getButtonData())) {
                        PushFileService pushFileService = applicationContext.getBean(PushFileService.class, event.getPath(), event.getFileId(), event.getDicId());
                        pushFileService.setOnSucceeded(e -> {
                            ResponseEntity<FileInfo> response = pushFileService.getValue();
                            if (HttpStatus.OK.equals(response.getStatusCode())) {
                                event.getPath().toFile().delete();
                            }
                        });
                        pushFileService.start();
                    }
                    showing=false;
                    return null;
                }
            });
            Platform.runLater(futureTask);
            try {
                futureTask.get();
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            } catch (ExecutionException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    private String resolveMessage(String key, Object... args){
        args = Optional.ofNullable(args).orElse(new Object[]{});
        return messageSource.getMessage(key,args,key, Locale.getDefault());
    }

}
