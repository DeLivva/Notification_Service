package com.vention.notification_service.service.rabbitmq.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vention.notification_service.dto.GeneralDto;
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
    public void processConfirmationTokenMessage(String msg){
        convertToNotificationDTOAndSave(msg);
    }

    @RabbitListener(queues = "${rabbitmq.queue.dispute-service}")
    public void processDisputeCreationMessage(String msg) {
        convertToNotificationDTOAndSave(msg);
    }

    private void convertToNotificationDTOAndSave(String msg) {
        try {
            GeneralDto<?> generalDto = objectMapper.readValue(msg, GeneralDto.class);
            Map body = (Map) generalDto.getBody();
            NotificationDTO dto = NotificationDTO.builder()
                    .title((String) body.get("title"))
                    .data(objectMapper.readValue(objectMapper.writeValueAsString(body.get("data")), Map.class))
                    .email(body.get("email").toString())
                    .build();
            service.save(dto, generalDto.getType(), false);
        } catch (JsonProcessingException e) {
            log.warn("Error while converting msg to JSON: " + e.getMessage());
        }
    }
}
