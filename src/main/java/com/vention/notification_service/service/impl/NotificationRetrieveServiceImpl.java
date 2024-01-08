package com.vention.notification_service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vention.notification_service.domain.NotificationEntity;
import com.vention.notification_service.domain.enums.NotificationType;
import com.vention.notification_service.dto.NotificationDTO;
import com.vention.notification_service.repository.NotificationRepository;
import com.vention.notification_service.service.NotificationRetrieveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationRetrieveServiceImpl implements NotificationRetrieveService {
    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<NotificationEntity> getNotSentNotifications() {
        return notificationRepository.findNotificationEntitiesByIsSent(false);
    }

    @Override
    public void save(NotificationDTO notification, NotificationType type, boolean sent) {
        try {
            NotificationEntity notificationEntity = NotificationEntity.builder()
                    .isSent(sent)
                    .title(notification.getTitle())
                    .data(objectMapper.writeValueAsString(notification.getData()))
                    .type(type)
                    .email(notification.getEmail())
                    .build();
            notificationRepository.save(notificationEntity);
        } catch (JsonProcessingException e) {
            log.warn("Error while converting to NotificationEntity: " + e.getMessage());
        }

    }

    @Async
    @Override
    public void makeSent(NotificationEntity notificationEntity) {
        notificationEntity.setIsSent(true);
        notificationRepository.save(notificationEntity);
    }
}
