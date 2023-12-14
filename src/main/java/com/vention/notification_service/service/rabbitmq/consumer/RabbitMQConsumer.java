package com.vention.notification_service.service.rabbitmq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vention.notification_service.dto.ConfirmationTokenDto;
import com.vention.notification_service.dto.GeneralDto;
import com.vention.notification_service.domain.NotificationType;
import com.vention.notification_service.service.MailSendingService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class RabbitMQConsumer {
    private final MailSendingService mailSendingService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = "${rabbitmq.queue.authorization-service}")
    public void processConfirmationTokenMessage(GeneralDto<?> generalDto) {
        NotificationType messageType = generalDto.getType();
        if (Objects.requireNonNull(messageType) == NotificationType.CONFIRMATION_TOKEN) {
            ConfirmationTokenDto confirmationTokenDto = objectMapper.convertValue(generalDto.getData(), ConfirmationTokenDto.class);
            mailSendingService.sendConfirmationToken(confirmationTokenDto);
        }
    }
}
