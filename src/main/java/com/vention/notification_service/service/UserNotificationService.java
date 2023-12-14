package com.vention.notification_service.service;

import com.vention.notification_service.domain.UserNotification;

import java.util.List;

public interface UserNotificationService {
    UserNotification saveUserNotification(UserNotification userNotification);
    List<UserNotification> getUserNotifications(Long userId);
}
