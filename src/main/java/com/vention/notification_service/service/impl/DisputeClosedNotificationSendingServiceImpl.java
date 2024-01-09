package com.vention.notification_service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vention.notification_service.domain.NotificationEntity;
import com.vention.notification_service.domain.enums.NotificationType;
import com.vention.notification_service.dto.DisputeClosedNotificationDTO;
import com.vention.notification_service.service.MailSendingService;
import com.vention.notification_service.service.NotificationRetrieveService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class DisputeClosedNotificationSendingServiceImpl implements MailSendingService {
    private final NotificationRetrieveService service;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${urls.dispute-api}")
    private String url;
    @Async
    @Override
    public void send(NotificationEntity notification) {
        String email = notification.getEmail();
        DisputeClosedNotificationDTO data = getObjectValues(notification.getData());
        String disputeLink = generateGetDisputeLink(Objects.requireNonNull(data).getOrderId());
        try {
            MimeMessage mailMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mailMessage, true);

            helper.setTo(email);
            helper.setSubject("Dispute closed");
            helper.setText(createDisputeClosedMessage(disputeLink), true);

            mailSender.send(mailMessage);
            service.makeSent(notification);
            log.info("Sent email to " + email);
        } catch (MessagingException e) {
            log.warn("Error occurred while sending email: " + email);
            log.warn(e.getLocalizedMessage());
        }
    }

    private DisputeClosedNotificationDTO getObjectValues(String data) {
        try {
            return objectMapper.readValue(data, DisputeClosedNotificationDTO.class);
        } catch (JsonProcessingException e) {
            log.warn("Error during processing json: " + e.getMessage());
        }
        return null;
    }

    private String generateGetDisputeLink(Long orderId) {
        return url + "?orderId=" + orderId;
    }

    private String createDisputeClosedMessage(String link) {
        Context context = new Context();
        context.setVariable("link", link);
        return templateEngine.process("dispute-closed", context);
    }

    @Override
    public NotificationType getType() {
        return NotificationType.DISPUTE_CLOSE;
    }
}
