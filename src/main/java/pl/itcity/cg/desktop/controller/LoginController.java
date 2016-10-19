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
import pl.itcity.cg.desktop.CgApplication;
import pl.itcity.cg.desktop.concurrent.LoginService;
import pl.itcity.cg.desktop.controller.common.ParentNodeAware;
import pl.itcity.cg.desktop.integration.service.JMSService;
import pl.itcity.cg.desktop.integration.service.TokenService;
import pl.itcity.cg.desktop.model.LoginResult;
import pl.itcity.cg.desktop.model.Principal;
import pl.itcity.cg.desktop.model.SessionContext;
import pl.itcity.cg.desktop.user.UserContext;

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

    @Resource
    private UserContext userContext;

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
                Principal principal = new Principal();
                principal.setEmail(login);
                principal.setPassword(password);
                loginService.setPrincipal(principal);
                loginService.setOnSucceeded(event -> {
                    LoginResult loginResult = loginService.getValue();
                    LOGGER.debug("login result obtained: "+loginResult);
                    if (loginResult.getJsonResponse().isSuccess()){
                        userContext.setAuthorized(true);
                        userContext.setContext(new SessionContext(principal.getEmail(), loginResult.getCookie()));
                        messageLabel.setText(messageSource.getMessage("login.succeeded",new Object[]{},Locale.getDefault()));

                        CgApplication.getInstance().goToConfig();
                    } else {
                        messageLabel.setText(messageSource.getMessage("login.failure",new Object[]{loginResult.getJsonResponse().getMessage()},Locale.getDefault()));
                    }
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
