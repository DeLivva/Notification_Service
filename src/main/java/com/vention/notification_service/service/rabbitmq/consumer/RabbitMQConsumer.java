package com.vention.notification_service.service.rabbitmq.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vention.notification_service.dto.GeneralDTO;
import com.vention.notification_service.dto.NotificationDTO;
import com.vention.notification_service.service.NotificationRetrieveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQConsumer {
    private final NotificationRetrieveService service;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = "${rabbitmq.queue.authorization-service}")
    public void processConfirmationTokenMessage(GeneralDTO<?> generalDto){
        convertToNotificationDTOAndSave(generalDto);
    }

    @RabbitListener(queues = "${rabbitmq.queue.dispute-service}")
    public void processDisputeCreationMessage(GeneralDTO<?> generalDto) {
        convertToNotificationDTOAndSave(generalDto);
    }

    @RabbitListener(queues = "${rabbitmq.queue.core-service}")
    public void processOrderStatusUpdateMessage(GeneralDTO<?> generalDto){
        convertToNotificationDTOAndSave(generalDto);
    }

    private void convertToNotificationDTOAndSave(GeneralDTO<?> generalDto) {
        try {
            Map body = (Map) generalDto.getBody();
            NotificationDTO dto = NotificationDTO.builder()
                    .title((String) body.get("title"))
                    .email((String) body.get("email"))
                    .data(objectMapper.readValue(objectMapper.writeValueAsString(body.get("data")), Map.class))
                    .build();
            service.save(dto, generalDto.getType(), false);
        } catch (JsonProcessingException e) {
            log.warn("Error while converting msg to JSON: " + e.getMessage());
        }
    }
}
