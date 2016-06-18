package pl.itcity.cg.desktop.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import pl.itcity.cg.desktop.concurrent.LoginService;
import pl.itcity.cg.desktop.controller.common.ParentNodeAware;
import pl.itcity.cg.desktop.model.LoginResult;
import pl.itcity.cg.desktop.model.Principal;

/**
 * login screen controller
 */
public class LoginController implements ParentNodeAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @FXML
    private Parent view;

    @FXML
    private TextField loginField;
    @FXML
    private TextField passwordField;
    @FXML
    private Label messageLabel;

    @Resource
    private MessageSource messageSource;

    @Resource
    private LoginService loginService;

    /**
     * performs login attempt
     */
    public void attemptLogin() {

        String login = loginField.getText();
        String password = passwordField.getText();

        List<String> errorMessages = new ArrayList<>();
        if (StringUtils.isEmpty(login)){
            errorMessages.add(messageSource.getMessage("login.error.emptyLogin", new Object[]{}, Locale.getDefault()));
        }
        if (StringUtils.isEmpty(password)){
            errorMessages.add(messageSource.getMessage("login.error.emptyPassword", new Object[]{}, Locale.getDefault()));
        }

        if (errorMessages.isEmpty()){
            if (loginService.isRunning()){
                LOGGER.warn("loginService allready running!");
            } else {
                messageLabel.setText(StringUtils.EMPTY);
                loginService.setOnSucceeded(event -> {
                    LoginResult loginResult = loginService.getValue();
                    LOGGER.debug("login result obtained: "+loginResult);
                    messageLabel.setText(messageSource.getMessage("login.succeeded",new Object[]{},Locale.getDefault()));
                    //todo [michal.adamczyk] add some after login logic - save session token, go to documents view etc (in Platform.runLater)
                });
                loginService.setOnFailed(event -> {
                    Throwable exception = loginService.getException();
                    LOGGER.error("exception while logging in:",exception);
                    messageLabel.setText(messageSource.getMessage("login.error.default",new Object[]{exception.getMessage()},Locale.getDefault()));
                });
                loginService.setPrincipal(new Principal(login, password));
                loginService.restart();
            }
        } else {
            messageLabel.setText(String.join("\n",errorMessages));
        }

    }

    @Override
    public Parent getView() {
        return view;
    }
}
