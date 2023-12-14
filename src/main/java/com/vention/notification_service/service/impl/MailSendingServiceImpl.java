package com.vention.notification_service.service.impl;

import com.vention.notification_service.dto.ConfirmationTokenDto;
import com.vention.notification_service.service.MailSendingService;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class MailSendingServiceImpl implements MailSendingService {

    private final JavaMailSender mailSender;
    private static final Logger log = LoggerFactory.getLogger(MailSendingServiceImpl.class);
    private final TemplateEngine templateEngine;

    @Override
    public void sendConfirmationToken(ConfirmationTokenDto tokenDto) {
        String confirmationLink = generateConfirmationLink(tokenDto);
        try {
            MimeMessage mailMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mailMessage, true);
            helper.setTo(tokenDto.getEmail());
            helper.setSubject("Confirmation");
            String message = createConfirmationMessage(confirmationLink);
            helper.setText(message, true);
            mailSender.send(mailMessage);
        } catch (MessagingException e) {
            log.error("Error occurred while sending confirmation token: ", e);
        }
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

