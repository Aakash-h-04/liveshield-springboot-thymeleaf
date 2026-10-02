package com.liveshield.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public EmailNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendAlertEmail(
            String recipient,
            String subject,
            String message
    ) {

        SimpleMailMessage email = new SimpleMailMessage();

        email.setFrom(senderEmail);
        email.setTo(recipient);
        email.setSubject(subject);
        email.setText(message);

        mailSender.send(email);
    }
}