package com.vention.notification_service.service;

import com.vention.notification_service.dto.ConfirmationTokenDto;

public interface MailSendingService {
    void sendConfirmationToken(ConfirmationTokenDto tokenDto);
}
