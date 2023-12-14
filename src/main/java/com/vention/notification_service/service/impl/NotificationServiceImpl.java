package com.vention.notification_service.service.impl;

import com.vention.notification_service.domain.Notification;
import com.vention.notification_service.repository.NotificationRepository;
import com.vention.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;

    @Override
    public Notification saveNotification(Notification notification) {
        return notificationRepository.save(notification);
    }
}
