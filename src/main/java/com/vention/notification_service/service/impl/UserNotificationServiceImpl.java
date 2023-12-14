package com.vention.notification_service.service.impl;

import com.vention.general.lib.exceptions.DataNotFoundException;
import com.vention.notification_service.domain.UserNotification;
import com.vention.notification_service.repository.UserNotificationRepository;
import com.vention.notification_service.service.UserNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserNotificationServiceImpl implements UserNotificationService {
    private final UserNotificationRepository userNotificationRepository;

    @Override
    public UserNotification saveUserNotification(UserNotification userNotification) {
        return userNotificationRepository.save(userNotification);
    }

    @Override
    public List<UserNotification> getUserNotifications(Long userId) {
        return userNotificationRepository.findAllByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found on id: " + userId));
    }
}
