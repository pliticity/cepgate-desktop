package pl.itcity.cg.desktop.integration.service.impl;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.amqp.inbound.AmqpInboundChannelAdapter;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;
import pl.itcity.cg.desktop.integration.service.JMSService;

import java.text.MessageFormat;

/**
 * {@link JMSService}
 *
 * @author Patryk Majchrzycki
 */
@Service
public class RabbitJMSService implements JMSService {

    private static final String QUEUE_PATTERN = "pl.iticity.{0}.queue";
    private static final String EXCHANGE_PATTERN = "pl.iticity.{0}.exchange";

    @Autowired
    private RabbitAdmin admin;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    @Qualifier(value = "fromRabbit")
    private MessageChannel fromRabbit;

    @Override
    public void initChannel(String token) {
        Queue queue = initQueue(token);
        DirectExchange exchange = initExchange(token);
        Binding binding = initBinding(queue, exchange);
    }

    private Queue initQueue(String desktopToken) {
        String queueName = getQueueName(desktopToken);
        Queue queue = new Queue(queueName, false, true, true);
        admin.declareQueue(queue);
        return queue;
    }

    private DirectExchange initExchange(String desktopToken) {
        String exchangeName = MessageFormat.format(EXCHANGE_PATTERN, desktopToken);
        DirectExchange exchange = new DirectExchange(exchangeName, false, true);
        admin.declareExchange(exchange);
        return exchange;
    }

    private Binding initBinding(Queue queue, DirectExchange exchange) {
        Binding binding = BindingBuilder.bind(queue).to(exchange).withQueueName();
        admin.declareBinding(binding);
        return binding;
    }

    private String getQueueName(String desktopToken) {
        return MessageFormat.format(QUEUE_PATTERN, desktopToken);
    }

    @Override
    public void connect(String token) {
        SimpleMessageListenerContainer container = initContainer(token);
        initAdapter(container, fromRabbit);
    }

    private SimpleMessageListenerContainer initContainer(String desktopToken) {
        SimpleMessageListenerContainer container =
                new SimpleMessageListenerContainer(connectionFactory);
        container.setQueueNames(getQueueName(desktopToken));
        return container;
    }

    private AmqpInboundChannelAdapter initAdapter(SimpleMessageListenerContainer listenerContainer, MessageChannel channel) {
        AmqpInboundChannelAdapter adapter = new AmqpInboundChannelAdapter(listenerContainer);
        adapter.setOutputChannel(channel);
        return adapter;
    }


}
