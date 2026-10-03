package com.liveshield.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import jakarta.annotation.PostConstruct;
import jakarta.mail.Message.RecipientType;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Service
public class EmailNotificationService {

    private static final String APPLICATION_NAME = "LiveShield";

    @Value("${google.gmail.client-id}")
    private String clientId;

    @Value("${google.gmail.client-secret}")
    private String clientSecret;

    @Value("${google.gmail.refresh-token}")
    private String refreshToken;

    @Value("${google.gmail.sender-email}")
    private String senderEmail;

    private Gmail gmailService;

    @PostConstruct
    public void initialize() {

        try {

            var httpTransport =
                    GoogleNetHttpTransport.newTrustedTransport();

            GoogleCredential credential =
                    new GoogleCredential.Builder()
                            .setTransport(httpTransport)
                            .setJsonFactory(
                                    GsonFactory.getDefaultInstance()
                            )
                            .setClientSecrets(
                                    clientId,
                                    clientSecret
                            )
                            .build()
                            .setRefreshToken(refreshToken);

            gmailService =
                    new Gmail.Builder(
                            httpTransport,
                            GsonFactory.getDefaultInstance(),
                            credential
                    )
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            System.out.println(
                    ">>> Gmail API initialized successfully"
            );

        } catch (Exception ex) {

            System.err.println(
                    ">>> Gmail API initialization failed: "
                            + ex.getMessage()
            );

            throw new IllegalStateException(
                    "Unable to initialize Gmail API",
                    ex
            );
        }
    }

    public void sendAlertEmail(
            String recipient,
            String subject,
            String messageText) {

        try {

            MimeMessage email =
                    createEmail(
                            recipient,
                            subject,
                            messageText
                    );

            ByteArrayOutputStream buffer =
                    new ByteArrayOutputStream();

            email.writeTo(buffer);

            String encodedEmail =
                    com.google.api.client.util.Base64
                            .encodeBase64URLSafeString(
                                    buffer.toByteArray()
                            );

            Message message = new Message();
            message.setRaw(encodedEmail);

            Message sentMessage =
                    gmailService
                            .users()
                            .messages()
                            .send("me", message)
                            .execute();

            System.out.println(
                    ">>> GMAIL API EMAIL SENT SUCCESSFULLY TO: "
                            + recipient
                            + " | ID: "
                            + sentMessage.getId()
            );

        } catch (Exception ex) {

            System.err.println(
                    ">>> GMAIL API EMAIL FAILED FOR "
                            + recipient
                            + ": "
                            + ex.getMessage()
            );

            throw new RuntimeException(
                    "Failed to send Gmail API email to "
                            + recipient,
                    ex
            );
        }
    }

    private MimeMessage createEmail(
            String recipient,
            String subject,
            String messageText) throws Exception {

        Properties properties = new Properties();

        Session session =
                Session.getDefaultInstance(
                        properties,
                        null
                );

        MimeMessage email =
                new MimeMessage(session);

        email.setFrom(
                new InternetAddress(senderEmail)
        );

        email.setRecipient(
                RecipientType.TO,
                new InternetAddress(recipient)
        );

        email.setSubject(
                subject,
                StandardCharsets.UTF_8.name()
        );

        email.setText(
                messageText,
                StandardCharsets.UTF_8.name()
        );

        return email;
    }
}