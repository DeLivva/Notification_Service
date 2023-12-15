package com.vention.notification_service.service;

import com.vention.notification_service.dto.ConfirmationTokenDto;
import com.vention.notification_service.dto.DisputeCreatedNotificationDTO;

public interface MailSendingService {
    void sendConfirmationToken(ConfirmationTokenDto tokenDto);

    void sendDisputeCreationMessage(DisputeCreatedNotificationDTO disputeCreationDto);
}
