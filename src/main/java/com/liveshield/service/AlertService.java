package com.liveshield.service;

import com.liveshield.entity.Alert;
import com.liveshield.entity.Farm;
import com.liveshield.entity.RiskAssessment;
import com.liveshield.entity.VeterinaryRecord;
import com.liveshield.repository.AlertRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Service
public class AlertService {

    private final AlertRepository repository;
    private final FarmService farmService;
    private final EmailNotificationService emailNotificationService;
    private final WhatsAppNotificationService whatsappNotificationService;

    @Value("${liveshield.admin.email}")
    private String adminEmail;

    public AlertService(
            AlertRepository repository,
            FarmService farmService,
            EmailNotificationService emailNotificationService,
            WhatsAppNotificationService whatsAppNotificationService) {

        this.repository = repository;
        this.farmService = farmService;
        this.emailNotificationService = emailNotificationService;
        this.whatsappNotificationService = whatsAppNotificationService;

    }

    public List<Alert> findAll() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    public List<Alert> findActive() {
        return repository.findByResolvedFalseOrderByCreatedAtDesc();
    }

    @Transactional
    public Alert createRiskAlert(RiskAssessment assessment) {

        System.out.println(
                ">>> createRiskAlert() CALLED - Risk Level: "
                        + assessment.getRiskLevel());

        Farm farm = farmService.findById(
                assessment.getFarm().getId());

        String severity = assessment.getRiskLevel();

        var latestRiskAlert = repository.findTopByFarmIdAndSourceOrderByCreatedAtDesc(
                farm.getId(),
                "RISK_ASSESSMENT");

        System.out.println(
                ">>> Checking previous risk alert for farm: "
                        + farm.getId());
        // Don't create duplicate alerts for the same risk level
        if (latestRiskAlert.isPresent()
                && severity.equalsIgnoreCase(
                        latestRiskAlert.get().getSeverity())) {

            System.out.println(
                    ">>> DUPLICATE RISK ALERT - No new alert/email");

            return latestRiskAlert.get();
        }

        Alert alert = new Alert();

        alert.setFarm(farm);
        alert.setSeverity(severity);
        alert.setSource("RISK_ASSESSMENT");

        switch (severity) {

            case "HIGH" -> {
                alert.setTitle("High biosecurity risk detected");
                alert.setDescription(
                        "The latest risk assessment indicates " +
                                "a high level of biosecurity risk. " +
                                "Review the priority actions and address " +
                                "the highest-risk controls.");
            }

            case "MEDIUM" -> {
                alert.setTitle("Biosecurity risk requires attention");
                alert.setDescription(
                        "The latest risk assessment indicates " +
                                "a medium level of biosecurity risk. " +
                                "Review the recommended corrective actions.");
            }

            default -> {
                alert.setTitle("Biosecurity risk assessment updated");
                alert.setDescription(
                        "The latest risk assessment indicates " +
                                "a low level of biosecurity risk.");
            }
        }

        Alert savedAlert = repository.save(alert);

        System.out.println(">>> NEW ALERT CREATED: " + savedAlert.getTitle());
        System.out.println(">>> ADMIN EMAIL: " + adminEmail);

        sendEmailNotification(savedAlert);

        System.out.println(">>> EMAIL SENT FOR ALERT: "
                + savedAlert.getTitle());

        return savedAlert;
    }

    @Transactional
    public Alert createVeterinaryAlert(
            VeterinaryRecord record) {

        Farm farm = record
                .getBatch()
                .getFarm();

        Alert alert = new Alert();

        alert.setFarm(farm);

        alert.setSeverity("HIGH");

        alert.setSource("VETERINARY");

        alert.setTitle(
                "Disease reported in livestock batch");

        alert.setDescription(
                "A disease or health condition has been " +
                        "reported for livestock batch " +
                        record.getBatch().getBatchCode() +
                        ". Review the veterinary record and " +
                        "take appropriate biosecurity measures.");

        Alert savedAlert = repository.save(alert);

        System.out.println(">>> NEW VETERINARY ALERT CREATED: "
                + savedAlert.getTitle());
        System.out.println(">>> ADMIN EMAIL: " + adminEmail);

        sendEmailNotification(savedAlert);

        System.out.println(">>> EMAIL SENT FOR ALERT: "
                + savedAlert.getTitle());

        return savedAlert;
    }

    @Transactional
    public void resolve(Long id) {

        Alert alert = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Alert not found: " + id));

        alert.setResolved(true);

        repository.save(alert);
    }

    private void sendEmailNotification(Alert alert) {

        String subject = "LiveShield Alert: " + alert.getTitle();

        String message = """
                LiveShield Alert

                Title: %s
                Severity: %s
                Source: %s

                Description:
                %s

                Farm: %s
                Farm ID: %s

                Please review the LiveShield dashboard for further details.
                """.formatted(
                alert.getTitle(),
                alert.getSeverity(),
                alert.getSource(),
                alert.getDescription(),
                alert.getFarm().getName(),
                alert.getFarm().getId());

        // Email notification
        try {
            emailNotificationService.sendAlertEmail(
                    adminEmail,
                    subject,
                    message);

            System.out.println(">>> ADMIN EMAIL SENT SUCCESSFULLY");

        } catch (Exception e) {
            System.err.println(
                    ">>> EMAIL NOTIFICATION FAILED: " + e.getMessage());
        }

        // WhatsApp notification
        try {
            whatsappNotificationService.sendAlertWhatsApp(message);

            System.out.println(">>> ADMIN WHATSAPP SENT SUCCESSFULLY");

        } catch (Exception e) {
            System.err.println(
                    ">>> WHATSAPP NOTIFICATION FAILED: " + e.getMessage());
        }
    }
}