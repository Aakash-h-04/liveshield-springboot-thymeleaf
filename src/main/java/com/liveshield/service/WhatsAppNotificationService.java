package com.liveshield.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WhatsAppNotificationService {

    private final String accountSid;
    private final String authToken;
    private final String fromNumber;
    private final String adminNumber;

    public WhatsAppNotificationService(
            @Value("${twilio.account-sid}") String accountSid,
            @Value("${twilio.auth-token}") String authToken,
            @Value("${twilio.whatsapp-from}") String fromNumber,
            @Value("${liveshield.admin.whatsapp}") String adminNumber) {

        this.accountSid = accountSid;
        this.authToken = authToken;
        this.fromNumber = fromNumber;
        this.adminNumber = adminNumber;

        Twilio.init(accountSid, authToken);
    }

    public void sendAlertWhatsApp(String messageText) {

        Message message = Message.creator(
                new PhoneNumber(adminNumber),
                new PhoneNumber(fromNumber),
                messageText
        ).create();

        System.out.println("WhatsApp message sent. SID: " + message.getSid());
    }
}