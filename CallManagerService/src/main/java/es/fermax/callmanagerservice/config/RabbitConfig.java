package es.fermax.callmanagerservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;

@Configuration
@EnableRabbit
public class RabbitConfig implements RabbitListenerConfigurer {

    private static final Boolean IS_DURABLE_QUEUE = true;

    public static final String QUEUE_ADD_INVITEE = "add-invitee-queue";
    public static final String QUEUE_START_RINGING = "start-ringing-queue";
    public static final String QUEUE_ACK_NOTIFICATION = "ack-notification-queue";

    @Value("${rabbitmq.exchange_name.add-invitee}")
    public String addInviteeExchange;

    @Value("${rabbitmq.exchange_name.start-ringing}")
    public String startRingingExchange;

    @Value("${rabbitmq.exchange_name.ack-notification}")
    public String ackNotificationExchange;


    @Bean
    Queue queueAddInvitee() {
        return new Queue(QUEUE_ADD_INVITEE, IS_DURABLE_QUEUE);
    }

    @Bean
    Queue queueStartRinging() {
        return new Queue(QUEUE_START_RINGING, IS_DURABLE_QUEUE);
    }

    @Bean
    FanoutExchange exchangeAddInvitee() {
        return new FanoutExchange(addInviteeExchange);
    }

    @Bean
    FanoutExchange exchangeStartRinging() {
        return new FanoutExchange(startRingingExchange);
    }

    @Bean
    FanoutExchange exchangeAckNotification() {
        return new FanoutExchange(ackNotificationExchange);
    }

    @Bean
    Binding bindingAddInvitee() {
        return BindingBuilder.bind(queueAddInvitee()).to(exchangeAddInvitee());
    }

    @Bean
    public MappingJackson2MessageConverter consumerJackson2MessageConverter() {
        return new MappingJackson2MessageConverter();
    }

    @Bean
    public DefaultMessageHandlerMethodFactory messageHandlerMethodFactory() {
        DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
        factory.setMessageConverter(consumerJackson2MessageConverter());
        return factory;
    }

    @Override
    public void configureRabbitListeners(final RabbitListenerEndpointRegistrar registrar) {
        registrar.setMessageHandlerMethodFactory(messageHandlerMethodFactory());
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {

        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
