package com.vention.notification_service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vention.general.lib.exceptions.BadRequestException;
import com.vention.notification_service.domain.NotificationEntity;
import com.vention.notification_service.domain.enums.NotificationType;
import com.vention.notification_service.dto.OrderOfferDTO;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderOfferSendingServiceImpl implements MailSendingService {
    private final NotificationRetrieveService service;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String CUSTOMER_DESCRIPTION = "A new offer from courier %s for your order with truck number %s.";
    private static final String COURIER_DESCRIPTION = "New offer from customer %s for order with truck number %s.";

    @Async
    @Override
    public void send(NotificationEntity notification) {
        OrderOfferDTO data = getObjectValues(notification.getData());
        if (data.getSender().equals("CUSTOMER")) {
            sendEmail(notification.getEmail(), createOrderOfferMessage(data, COURIER_DESCRIPTION), notification);
        } else {
            sendEmail(notification.getEmail(), createOrderOfferMessage(data, CUSTOMER_DESCRIPTION), notification);
        }
    }

    private void sendEmail(String recipientEmail, String text, NotificationEntity notification) {
        try {
            MimeMessage mailMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mailMessage, true);

            helper.setTo(recipientEmail);
            helper.setSubject("Order offer");
            helper.setText(text, true);

            mailSender.send(mailMessage);
            service.makeSent(notification);
            log.info("Sent email to " + recipientEmail);
        } catch (MessagingException e) {
            log.warn("Error occurred while sending email: " + recipientEmail);
            log.warn(e.getLocalizedMessage());
        }
    }

    private OrderOfferDTO getObjectValues(String data) {
        try {
            return objectMapper.readValue(data, OrderOfferDTO.class);
        } catch (JsonProcessingException e) {
            log.warn("Error during processing json: " + e.getMessage());
            throw new BadRequestException("Error during processing json: " + e.getMessage());
        }
    }

    private String createOrderOfferMessage(OrderOfferDTO dto, String description) {
        Context context = new Context();
        context.setVariable("description", String.format(description, dto.getSenderName(), dto.getTrackNumber()));
        context.setVariable("senderName", dto.getSenderName());
        context.setVariable("startingPlace", dto.getStartLocation());
        context.setVariable("finalPlace", dto.getFinalLocation());
        context.setVariable("date", dto.getDeliveryDate());
        context.setVariable("itemDescription", dto.getDescription());
        context.setVariable("approveLink", generateApproveLink(dto));
        context.setVariable("rejectLink", generateRejectLink(dto));
        return templateEngine.process("order-offer", context);
    }

    private String generateApproveLink(OrderOfferDTO orderOfferDTO) {
        return generateLink(orderOfferDTO, "ORDER_OFFER_APPROVE_URL");
    }

    private String generateRejectLink(OrderOfferDTO orderOfferDTO) {
        return generateLink(orderOfferDTO, "ORDER_OFFER_REJECT_URL");
    }
    private String generateLink(OrderOfferDTO orderOfferDTO, String urlKey) {
        Dotenv dotenv = Dotenv.load();
        String url = dotenv.get(urlKey);
        return url + "?userId=" + orderOfferDTO.getUserId() + "&orderId=" + orderOfferDTO.getId();
    }
    @Override
    public NotificationType getType() {
        return NotificationType.ORDER_OFFER;
    }
}
