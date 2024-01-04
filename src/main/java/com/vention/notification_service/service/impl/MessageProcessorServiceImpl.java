package com.vention.notification_service.service.impl;

import com.vention.notification_service.domain.NotificationEntity;
import com.vention.notification_service.domain.enums.NotificationType;
import com.vention.notification_service.service.MailSendingService;
import com.vention.notification_service.service.MessageProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageProcessorServiceImpl implements MessageProcessorService {
    private final List<MailSendingService> services;

    @Override
    public void process(NotificationEntity notification) {
        getServiceByType(notification.getType()).send(notification);
    }

    private MailSendingService getServiceByType(NotificationType type) {
        return services.stream().filter(a -> a.getType() == type).findFirst().get();
    }
}
