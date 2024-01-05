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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DisputeCreatedNotificationSendingServiceImpl implements MailSendingService {
    private static final Logger log = LoggerFactory.getLogger(DisputeCreatedNotificationSendingServiceImpl.class);
    private static final String HEADER_TEXT = "Dispute was created for order number #%d.";
    private static final String FROM_TO = "Dispute was generated from %s to %s.";
    private final NotificationRetrieveService service;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final NotificationType type = NotificationType.DISPUTE_CREATION;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Async
    @Override
    public void send(NotificationEntity notification) {
        String email = notification.getEmail();
        DisputeCreatedNotificationDTO data = getObjectValues(notification.getData());
        String disputeLink = generateGetDisputeLink(Objects.requireNonNull(data).getOrderId());
        try {
            MimeMessage mailMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mailMessage, true);

            helper.setTo(email);
            helper.setSubject("Dispute created");
            helper.setText(createDisputeCreatedMessage(data.getOrderId(), disputeLink,
                    data.getDescription(), data.getOwnerName(), data.getDriverName()), true);

            mailSender.send(mailMessage);
            service.makeSent(notification);
            log.info("Sent email to " + email);
        } catch (MessagingException e) {
            log.warn("Error occurred while sending email: " + email);
            log.warn(e.getLocalizedMessage());
        }
        // send dispute notification to admins

        System.out.println(data.getAdminEmails());
        for (String e : data.getAdminEmails()) {
            try {
                MimeMessage mailMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mailMessage, true);

                helper.setTo(e);
                helper.setSubject("Dispute created");
                helper.setText(createDisputeCreatedMessage(data.getOrderId(), disputeLink,
                        data.getDescription(), data.getOwnerName(), data.getDriverName()), true);
                mailSender.send(mailMessage);
                log.info("Sent email to admin " + e);
            } catch (MessagingException ex) {
                log.warn("Error occurred while sending email: " + e);
                log.warn(ex.getLocalizedMessage());
            }
        }
    }

    @Override
    public NotificationType getType() {
        return this.type;
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

    private String createDisputeCreatedMessage(Long orderId, String link, String description, String from, String to) {
        Context context = new Context();
        context.setVariable("link", link);
        context.setVariable("headerText", String.format(HEADER_TEXT, orderId));
        context.setVariable("description", description);
        context.setVariable("disputeFromTo", String.format(FROM_TO, from, to));
        return templateEngine.process("dispute-created", context);
    }
}
