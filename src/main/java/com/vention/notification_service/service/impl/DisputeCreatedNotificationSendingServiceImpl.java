package com.vention.notification_service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vention.notification_service.domain.NotificationEntity;
import com.vention.notification_service.domain.enums.NotificationType;
import com.vention.notification_service.dto.DisputeCreatedNotificationDTO;
import com.vention.notification_service.service.MailSendingService;
import com.vention.notification_service.service.NotificationRetrieveService;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class DisputeCreatedNotificationSendingServiceImpl implements MailSendingService {
    private static final String HEADER_TEXT = "Dispute was created for order number #%d.";
    private static final String FROM_TO = "Dispute was generated from %s to %s.";
    private final NotificationRetrieveService service;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Async
    @Override
    public void send(NotificationEntity notification) {
        DisputeCreatedNotificationDTO data = getObjectValues(notification.getData());
        String disputeLink = generateGetDisputeLink(Objects.requireNonNull(data).getOrderId());
        String subject = "Dispute created";

        // Send notification to courier
        sendEmail(notification.getEmail(), subject, createDisputeCreatedMessage(disputeLink, data), notification);

        // Send dispute notification to admins
        for (String adminEmail : data.getAdminEmails()) {
            sendEmail(adminEmail, subject, createDisputeCreatedMessage(disputeLink, data), notification);
        }
    }

    private void sendEmail(String emailTo, String subject, String text, NotificationEntity notification) {
        try {
            MimeMessage mailMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mailMessage, true);

            helper.setTo(emailTo);
            helper.setSubject(subject);
            helper.setText(text, true);

            mailSender.send(mailMessage);
            service.makeSent(notification);
            log.info("Sent email to " + emailTo);
        } catch (MessagingException e) {
            log.warn("Error occurred while sending email: " + emailTo);
            log.warn(e.getLocalizedMessage());
        }
    }

    @Override
    public NotificationType getType() {
        return NotificationType.DISPUTE_CREATION;
    }

    private DisputeCreatedNotificationDTO getObjectValues(String data) {
        try {
            return objectMapper.readValue(data, DisputeCreatedNotificationDTO.class);
        } catch (JsonProcessingException e) {
            log.warn("Error during processing json: " + e.getMessage());
        }
        return null;
    }

    private String generateGetDisputeLink(Long orderId) {
        Dotenv dotenv = Dotenv.load();
        String url = dotenv.get("DISPUTE_API_URL");
        return url + "?orderId=" + orderId;
    }

    private String createDisputeCreatedMessage(String link, DisputeCreatedNotificationDTO data) {
        Context context = new Context();
        context.setVariable("link", link);
        context.setVariable("headerText", String.format(HEADER_TEXT, data.getOrderId()));
        context.setVariable("description", data.getDescription());
        context.setVariable("disputeFromTo", String.format(FROM_TO, data.getOwnerName(), data.getDriverName()));
        return templateEngine.process("dispute-created", context);
    }
}
