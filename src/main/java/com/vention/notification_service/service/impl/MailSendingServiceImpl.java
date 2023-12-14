package com.vention.notification_service.service.impl;

import com.vention.general.lib.exceptions.BadRequestException;
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

@Service
@RequiredArgsConstructor
public class MailSendingServiceImpl implements MailSendingService {

    private final JavaMailSender mailSender;
    private static final Logger log = LoggerFactory.getLogger(MailSendingServiceImpl.class);

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
            throw new BadRequestException(e.getMessage());
        }
    }

    private String generateConfirmationLink(ConfirmationTokenDto tokenDto) {
        Dotenv dotenv = Dotenv.load();
        String url = dotenv.get("CONFIRMATION_API_URL");
        return url + "?token=" + tokenDto.getToken();
    }

    private String createConfirmationMessage(String link) {
        return "<html><body>" +
                "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Email Confirmation</title>\n" +
                "</head>\n" +
                "<body style=\"font-family: Arial, sans-serif;\">\n" +
                "<div style=\"max-width: 600px; margin: auto; padding: 20px; text-align: center; background-color: #f4f4f4; border-radius: 10px;\">\n" +
                "    <h2 style=\"color: #333;\">Confirm your email</h2>\n" +
                "    <p style=\"color: #666;\">Please click on the button to confirm your email:</p>\n" +
                "    <a href=\"" + link + "\" style=\"text-decoration: none;\">\n" +
                "        <button style=\"display: inline-block; padding: 10px 20px; font-size: 16px; background-color: #007bff; color: #fff; border: none; border-radius: 5px; cursor: pointer;\">Confirm</button>\n" +
                "    </a>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>" +
                "</body></html>";
    }
}

