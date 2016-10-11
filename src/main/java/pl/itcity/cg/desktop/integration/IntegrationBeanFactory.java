package pl.itcity.cg.desktop.integration;

import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import pl.itcity.cg.desktop.CgApplication;
import pl.itcity.cg.desktop.concurrent.PullFileService;
import pl.itcity.cg.desktop.integration.service.FileHelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Optional;

/**
 * A factory class for spring integration beans (RabbitMQ)
 *
 * @author Patryk Majchrzycki
 */
@Configuration
@PropertySource("classpath:integration.properties")
@EnableIntegration
@IntegrationComponentScan
public class IntegrationBeanFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationBeanFactory.class);

    @Autowired
    private Environment environment;

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public ConnectionFactory connectionFactory() throws Exception {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost(environment.getProperty("cepgate.rabbit.host"));
        factory.setUsername(environment.getProperty("cepgate.rabbit.username"));
        factory.setPassword(environment.getProperty("cepgate.rabbit.password"));
        factory.setVirtualHost(environment.getProperty("cepgate.rabbit.vh"));
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) throws Exception {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    public RabbitAdmin rabbitAdmin() throws Exception {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    public MessageChannel fromRabbit() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "fromRabbit")
    public MessageHandler messageHandler() {
        return new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                String payload = new String((byte[]) message.getPayload());
                PullFileService pullFileService = applicationContext.getBean(PullFileService.class);
                pullFileService.setFileSymbol(payload);
                pullFileService.setOnFailed(event -> {
                    LOGGER.error(MessageFormat.format("Could not pull file with symbol {0}", payload), pullFileService.getException());
                });
                pullFileService.setOnSucceeded(event -> {
                    byte[] fileBytes = pullFileService.getValue().getBody();
                    FileChooser fileChooser = FileHelper.initFileChooser(FileHelper.getFileName(pullFileService.getValue()));
                    File file = fileChooser.showSaveDialog(CgApplication.getInstance()
                            .getMainStage());
                    Optional.ofNullable(file).ifPresent(result -> {
                        String path = result.getPath();

                        try {
                            Files.write(Paths.get(path),fileBytes);
                        } catch (IOException e) {
                            LOGGER.error(e.getMessage(),e);
                            return;
                        }
                    });
                    LOGGER.info(MessageFormat.format("Pulled file with symbol {0}",payload));
                });
                pullFileService.start();
            }

        };
    }

}
