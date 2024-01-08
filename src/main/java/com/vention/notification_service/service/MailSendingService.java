package com.vention.notification_service.service;

import com.vention.notification_service.domain.NotificationEntity;
import com.vention.notification_service.domain.enums.NotificationType;

public interface MailSendingService {
    void send(NotificationEntity notification);
    NotificationType getType();
}
