package pl.itcity.cg.desktop;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javafx.fxml.FXMLLoader;
import pl.itcity.cg.desktop.controller.DocumentListController;
import pl.itcity.cg.desktop.controller.LoginController;

/**
 * A factory of javaFX controllers as spring beans
 *
 * @author Michal Adamczyk
 */
@Configuration
@ComponentScan(basePackages = {"pl.itcity.cg.desktop"})
@PropertySource("classpath:application.properties")
public class AppBeanFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppBeanFactory.class);
    private static final String BUNDLE_LOCALE = "bundle.locale";

    @Autowired
    private Environment environment;

    @Autowired(required = false)
    private List<ClientHttpRequestInterceptor> requestInterceptors;

    @Bean
    public LoginController loginController() throws IOException {
        return (LoginController) loadController("/fxml/login.fxml");
    }

    @Bean
    public DocumentListController documentListController() throws IOException {
        return (DocumentListController) loadController("/fxml/documentListVIew.fxml");
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

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        if (requestInterceptors != null) {
            restTemplate.setInterceptors(requestInterceptors);
        }
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        BufferingClientHttpRequestFactory requestFactory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
        restTemplate.setRequestFactory(requestFactory);
        return restTemplate;
    }

    @Bean
    public String baseUrl(){
        return environment.getProperty("cepgate.service.baseUrl");
    }
}
