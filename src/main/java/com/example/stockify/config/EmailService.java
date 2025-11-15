package com.example.stockify.config;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username")
    private String fromEmail;

    @Value("${app.email.admin}")
    private String adminEmail;


    public void enviarEmailHtmlAlAdmin(String asunto, String contenidoHtml) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(adminEmail);
            helper.setSubject(asunto);
            helper.setText(contenidoHtml, true); // true = HTML

            mailSender.send(mimeMessage);
            log.info("✓ Email HTML enviado exitosamente a: {}", adminEmail);
        } catch (Exception e) {
            log.error("✗ Error al enviar email HTML: {}", e.getMessage());
        }
    }
}

