package pl.itcity.cg.desktop.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
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

import java.text.MessageFormat;

/**
 * A factory class for spring integration beans (RabbitMQ)
 *
 * @author Patryk Majchrzycki
 */
@Configuration
@ComponentScan(basePackages = {"pl.itcity.cg.desktop.integration"})
@PropertySource("classpath:integration.properties")
@EnableIntegration
@IntegrationComponentScan
public class IntegrationBeanFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationBeanFactory.class);

    @Autowired
    private Environment environment;

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
                LOGGER.info(MessageFormat.format("Fetched {0} message", message.getPayload()));
            }

        };
    }

}
