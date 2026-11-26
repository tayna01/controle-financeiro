package br.com.financeiro.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromAddress;

    public boolean isConfigured() {
        return mailSender != null && fromAddress != null && !fromAddress.isBlank();
    }

    public void sendSimple(String to, String subject, String text) {
        if (!isConfigured()) {
            log.warn("Email nao configurado; envio ignorado para {}", to);
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
        log.info("Email simples enviado para {}", to);
    }

    public void sendHtml(String to, String subject, String html) {
        if (!isConfigured()) {
            log.warn("Email nao configurado; envio ignorado para {}", to);
            return;
        }
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(mimeMessage);
            log.info("Email HTML enviado para {}", to);
        } catch (MessagingException ex) {
            log.error("Falha ao enviar email HTML para {}", to, ex);
            throw new RuntimeException("Falha ao enviar e-mail", ex);
        }
    }
}