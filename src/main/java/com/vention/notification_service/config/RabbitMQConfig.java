package com.vention.notification_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.vention.notification_service.config.RabbitConstants.DIRECT_ERROR_EXCHANGE_NAME;
import static com.vention.notification_service.config.RabbitConstants.DIRECT_EXCHANGE_NAME;
import static com.vention.notification_service.config.RabbitConstants.NOTIFICATION_SERVICE_ERROR_QUEUE;
import static com.vention.notification_service.config.RabbitConstants.NOTIFICATION_SERVICE_ERROR_ROUTING_KEY;
import static com.vention.notification_service.config.RabbitConstants.NOTIFICATION_SERVICE_QUEUE;
import static com.vention.notification_service.config.RabbitConstants.NOTIFICATION_SERVICE_ROUTING_KEY;

@Configuration
@RequiredArgsConstructor
@EnableRabbit
public class RabbitMQConfig {
    /**
     * DECLARING EXCHANGES
     */

    @Bean
    public DirectExchange getNotificationExchange() {
        return new DirectExchange(DIRECT_EXCHANGE_NAME, true, false);
    }

    @Bean
    public DirectExchange getNotificationErrorExchange() {
        return new DirectExchange(DIRECT_ERROR_EXCHANGE_NAME, true, false);
    }

    @Bean
    public MessageConverter rabbitJsonConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * DECLARING QUEUE
     */
    @Bean
    public Queue notificationServiceQueue() {
        return QueueBuilder.durable(NOTIFICATION_SERVICE_QUEUE)
                .withArgument("x-dead-letter-exchange", DIRECT_ERROR_EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key",NOTIFICATION_SERVICE_ERROR_ROUTING_KEY).build();
    }
    /**
     * DECLARING ERROR QUEUE
     */
    @Bean
    public Queue notificationServiceErrorQueue() {
        return QueueBuilder.durable(NOTIFICATION_SERVICE_ERROR_QUEUE).build();
    }

    /**
     * BINDING QUEUE
     */
    @Bean
    public Binding bindingNotificationServiceQueue() {
        return BindingBuilder.bind(notificationServiceQueue()).to(getNotificationExchange()).with(NOTIFICATION_SERVICE_ROUTING_KEY);
    }

    /**
     * BINDING ERROR QUEUE
     */
    @Bean
    public Binding bindingNotificationServiceErrorQueue() {
        return BindingBuilder.bind(notificationServiceErrorQueue())
                .to(getNotificationErrorExchange())
                .with(NOTIFICATION_SERVICE_ERROR_ROUTING_KEY);
    }
}