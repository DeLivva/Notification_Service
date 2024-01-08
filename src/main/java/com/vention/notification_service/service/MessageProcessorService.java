package com.vention.notification_service.service;

import com.vention.notification_service.domain.NotificationEntity;

public interface MessageProcessorService {
    void process(NotificationEntity notification);
}
