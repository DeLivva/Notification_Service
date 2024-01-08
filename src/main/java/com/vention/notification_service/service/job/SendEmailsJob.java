package com.vention.notification_service.service.job;

import com.vention.notification_service.domain.NotificationEntity;
import com.vention.notification_service.service.MessageProcessorService;
import com.vention.notification_service.service.NotificationRetrieveService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SendEmailsJob {
    private final NotificationRetrieveService service;
    private final MessageProcessorService messageProcessorService;

    @Scheduled(fixedRate = 15000)
    public void sendEmails() {
        for (NotificationEntity notification: service.getNotSentNotifications()) {
            messageProcessorService.process(notification);
        }
    }
}
