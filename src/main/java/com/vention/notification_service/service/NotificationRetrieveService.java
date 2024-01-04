package com.vention.notification_service.service;

import com.vention.notification_service.domain.NotificationEntity;
import com.vention.notification_service.domain.enums.NotificationType;
import com.vention.notification_service.dto.NotificationDTO;

import java.util.List;

public interface NotificationRetrieveService {
    List<NotificationEntity> getNotSentNotifications();
    void save(NotificationDTO notification, NotificationType type, boolean sent);
    void makeSent(NotificationEntity notificationEntity);
}
