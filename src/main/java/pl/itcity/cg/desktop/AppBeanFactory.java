package pl.itcity.cg.desktop;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

import javafx.fxml.FXMLLoader;
import pl.itcity.cg.desktop.controller.LoginController;

/**
 * A factory of javaFX controllers as spring beans
 *
 * @author Michal Adamczyk
 */
@Configuration
@ComponentScan(basePackages = {"pl.itcity.cg.desktop"})
public class AppBeanFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppBeanFactory.class);
    private static final String BUNDLE_LOCALE = "bundle.locale";

    @Bean
    public LoginController loginController() throws IOException {
        return (LoginController) loadController("/fxml/login.fxml");
    }

    private Object loadController(String url) throws IOException {
        URL resource = getClass().getResource(url);
        LOGGER.info("resource: ",resource);
        FXMLLoader fxmlLoader = new FXMLLoader(resource, ResourceBundle.getBundle(BUNDLE_LOCALE, Locale.getDefault()));
        fxmlLoader.load();
        return fxmlLoader.getController();
    }

    @Bean
    public MessageSource messageSource(){
        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setBasename(BUNDLE_LOCALE);
        return resourceBundleMessageSource;
    }
}
