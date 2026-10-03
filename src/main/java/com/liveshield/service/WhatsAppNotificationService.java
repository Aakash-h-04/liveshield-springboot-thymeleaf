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
    private final String[] adminNumbers;

    public WhatsAppNotificationService(
            @Value("${twilio.account-sid}") String accountSid,
            @Value("${twilio.auth-token}") String authToken,
            @Value("${twilio.whatsapp-from}") String fromNumber,
            @Value("${liveshield.admin.whatsapp-numbers}") String adminNumbers) {

        this.accountSid = accountSid;
        this.authToken = authToken;
        this.fromNumber = fromNumber;

        this.adminNumbers = adminNumbers
                .split(",");

        for (int i = 0; i < this.adminNumbers.length; i++) {
            this.adminNumbers[i] = this.adminNumbers[i].trim();
        }

        Twilio.init(accountSid, authToken);
    }

    public void sendAlertWhatsApp(String messageText) {

        for (String adminNumber : adminNumbers) {

            if (adminNumber.isBlank()) {
                continue;
            }

            try {

                Message message = Message.creator(
                        new PhoneNumber(adminNumber),
                        new PhoneNumber(fromNumber),
                        messageText
                ).create();

                System.out.println(
                        "WhatsApp message sent to "
                        + adminNumber
                        + ". SID: "
                        + message.getSid()
                );

            } catch (Exception ex) {

                System.err.println(
                        "Failed to send WhatsApp message to "
                        + adminNumber
                        + ": "
                        + ex.getMessage()
                );
            }
        }
    }
}