package com.vention.notification_service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vention.notification_service.domain.NotificationEntity;
import com.vention.notification_service.domain.enums.NotificationType;
import com.vention.notification_service.dto.ConfirmationTokenDto;
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
public class ConfirmationTokenSendingServiceImpl implements MailSendingService {
    private static final Logger log = LoggerFactory.getLogger(ConfirmationTokenSendingServiceImpl.class);
    private final NotificationRetrieveService service;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final NotificationType type = NotificationType.CONFIRMATION_TOKEN;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Async
    @Override
    public void send(NotificationEntity notification) {
        String email = notification.getEmail();
        ConfirmationTokenDto data = getObjectValues(notification.getData());
        String confirmationLink = generateConfirmationLink(Objects.requireNonNull(data));
        try {
            MimeMessage mailMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mailMessage, true);

            helper.setTo(email);
            helper.setSubject("Confirmation");
            helper.setText(createConfirmationMessage(confirmationLink), true);

            mailSender.send(mailMessage);
            service.makeSent(notification);
            log.info("Sent email to " + email);
        } catch (MessagingException e) {
            log.warn("Error occurred while sending email: " + email);
            log.warn(e.getLocalizedMessage());
        }
    }

    private ConfirmationTokenDto getObjectValues(String obj) {
        try {
            return objectMapper.readValue(obj, ConfirmationTokenDto.class);
        } catch (JsonProcessingException e) {
            log.warn("Error during processing json: " + e.getMessage());
        }
        return null;
    }

    @Override
    public NotificationType getType() {
        return this.type;
    }

    private String generateConfirmationLink(ConfirmationTokenDto tokenDto) {
        Dotenv dotenv = Dotenv.load();
        String url = dotenv.get("CONFIRMATION_API_URL");
        return url + "?token=" + tokenDto.getToken();
    }

    private String createConfirmationMessage(String link) {
        Context context = new Context();
        context.setVariable("link", link);
        return templateEngine.process("confirmation-token", context);
    }
}
