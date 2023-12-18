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

import static com.vention.notification_service.config.RabbitConstants.AUTHORIZATION_SERVICE_ERROR_QUEUE;
import static com.vention.notification_service.config.RabbitConstants.AUTHORIZATION_SERVICE_ERROR_ROUTING_KEY;
import static com.vention.notification_service.config.RabbitConstants.AUTHORIZATION_SERVICE_QUEUE;
import static com.vention.notification_service.config.RabbitConstants.AUTHORIZATION_SERVICE_ROUTING_KEY;
import static com.vention.notification_service.config.RabbitConstants.CORE_SERVICE_ERROR_QUEUE;
import static com.vention.notification_service.config.RabbitConstants.CORE_SERVICE_ERROR_ROUTING_KEY;
import static com.vention.notification_service.config.RabbitConstants.CORE_SERVICE_QUEUE;
import static com.vention.notification_service.config.RabbitConstants.CORE_SERVICE_ROUTING_KEY;
import static com.vention.notification_service.config.RabbitConstants.DIRECT_ERROR_EXCHANGE_NAME;
import static com.vention.notification_service.config.RabbitConstants.DIRECT_EXCHANGE_NAME;
import static com.vention.notification_service.config.RabbitConstants.DISPUTE_SERVICE_ERROR_QUEUE;
import static com.vention.notification_service.config.RabbitConstants.DISPUTE_SERVICE_ERROR_ROUTING_KEY;
import static com.vention.notification_service.config.RabbitConstants.DISPUTE_SERVICE_QUEUE;
import static com.vention.notification_service.config.RabbitConstants.DISPUTE_SERVICE_ROUTING_KEY;

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
     * DECLARING QUEUES
     */
    @Bean
    public Queue authorizationServiceQueue() {
        return QueueBuilder.durable(AUTHORIZATION_SERVICE_QUEUE)
                .withArgument("x-dead-letter-exchange", DIRECT_ERROR_EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", AUTHORIZATION_SERVICE_ERROR_ROUTING_KEY).build();
    }

    @Bean
    public Queue coreServiceQueue() {
        return QueueBuilder.durable(CORE_SERVICE_QUEUE)
                .withArgument("x-dead-letter-exchange", DIRECT_ERROR_EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", CORE_SERVICE_ERROR_ROUTING_KEY).build();
    }

    @Bean
    public Queue disputeServiceQueue() {
        return QueueBuilder.durable(DISPUTE_SERVICE_QUEUE)
                .withArgument("x-dead-letter-exchange", DIRECT_ERROR_EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", DISPUTE_SERVICE_ERROR_ROUTING_KEY).build();
    }

    /**
     * DECLARING ERROR QUEUES
     */
    @Bean
    public Queue authorizationServiceErrorQueue() {
        return QueueBuilder.durable(AUTHORIZATION_SERVICE_ERROR_QUEUE).build();
    }

    @Bean
    public Queue coreServiceErrorQueue() {
        return QueueBuilder.durable(CORE_SERVICE_ERROR_QUEUE).build();
    }

    @Bean
    public Queue disputeServiceErrorQueue() {
        return QueueBuilder.durable(DISPUTE_SERVICE_ERROR_QUEUE).build();
    }

    /**
     * BINDING QUEUES
     */
    @Bean
    public Binding bindingAuthorizationServiceQueue() {
        return BindingBuilder.bind(authorizationServiceQueue()).to(getNotificationExchange()).with(AUTHORIZATION_SERVICE_ROUTING_KEY);
    }

    @Bean
    public Binding bindingDisputeServiceQueue() {
        return BindingBuilder.bind(disputeServiceQueue()).to(getNotificationExchange()).with(DISPUTE_SERVICE_ROUTING_KEY);
    }

    @Bean
    public Binding bindingCoreServiceQueue() {
        return BindingBuilder.bind(coreServiceQueue()).to(getNotificationExchange()).with(CORE_SERVICE_ROUTING_KEY);
    }

    /**
     * BINDING ERROR QUEUES
     */
    @Bean
    public Binding bindingAuthorizationServiceErrorQueue() {
        return BindingBuilder.bind(authorizationServiceErrorQueue())
                .to(getNotificationErrorExchange())
                .with(AUTHORIZATION_SERVICE_ERROR_ROUTING_KEY);
    }

    @Bean
    public Binding bindingDisputeServiceErrorQueue() {
        return BindingBuilder.bind(disputeServiceErrorQueue())
                .to(getNotificationErrorExchange())
                .with(DISPUTE_SERVICE_ERROR_ROUTING_KEY);
    }

    @Bean
    public Binding bindingCoreServiceErrorQueue() {
        return BindingBuilder.bind(coreServiceErrorQueue())
                .to(getNotificationErrorExchange())
                .with(CORE_SERVICE_ERROR_ROUTING_KEY);
    }
}