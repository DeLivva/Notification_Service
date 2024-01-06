package com.vention.notification_service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vention.general.lib.exceptions.BadRequestException;
import com.vention.notification_service.domain.NotificationEntity;
import com.vention.notification_service.domain.enums.NotificationType;
import com.vention.notification_service.dto.OrderStatusChangeDTO;
import com.vention.notification_service.service.MailSendingService;
import com.vention.notification_service.service.NotificationRetrieveService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@AllArgsConstructor
public class OrderStatusChangeSendingServiceImpl implements MailSendingService {
    private static final Logger log = LoggerFactory.getLogger(OrderStatusChangeSendingServiceImpl.class);
    private final NotificationRetrieveService service;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final NotificationType type = NotificationType.ORDER_STATUS_CHANGE;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String CUSTOMER_DESCRIPTION = "Your order with truck number %s changed status to %s.";
    private static final String COURIER_DESCRIPTION = "The order you have picked up with truck number %s changed status to %s.";

    @Async
    @Override
    public void send(NotificationEntity notification) {
        OrderStatusChangeDTO data = getObjectValues(notification.getData());
        sendEmail(notification.getEmail(), createOrderStatusChangeMessage(data, CUSTOMER_DESCRIPTION), notification);
        sendEmail(data.getDriverEmail(), createOrderStatusChangeMessage(data, COURIER_DESCRIPTION), notification);
    }

    private void sendEmail(String recipientEmail, String text, NotificationEntity notification) {
        try {
            MimeMessage mailMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mailMessage, true);

            helper.setTo(recipientEmail);
            helper.setSubject("Order Status updated");
            helper.setText(text, true);

            mailSender.send(mailMessage);
            service.makeSent(notification);
            log.info("Sent email to " + recipientEmail);
        } catch (MessagingException e) {
            log.warn("Error occurred while sending email: " + recipientEmail);
            log.warn(e.getLocalizedMessage());
        }
    }

    private OrderStatusChangeDTO getObjectValues(String data) {
        try {
            return objectMapper.readValue(data, OrderStatusChangeDTO.class);
        } catch (JsonProcessingException e) {
            log.warn("Error during processing json: " + e.getMessage());
            throw new BadRequestException("Error during processing json: " + e.getMessage());
        }
    }

    private String createOrderStatusChangeMessage(OrderStatusChangeDTO dto, String description) {
        Context context = new Context();
        context.setVariable("description", String.format(description, dto.getTrackNumber(),  dto.getStatus()));
        return templateEngine.process("order-status-change", context);
    }

    @Override
    public NotificationType getType() {
        return this.type;
    }
}
