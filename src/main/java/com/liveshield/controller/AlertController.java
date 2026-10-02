package com.liveshield.controller;

import com.liveshield.service.AlertService;
import com.liveshield.service.EmailNotificationService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AlertController {

    private final AlertService alertService;
    private final EmailNotificationService emailNotificationService;

    public AlertController(AlertService alertService, EmailNotificationService emailNotificationService) {
        this.alertService = alertService;
        this.emailNotificationService = emailNotificationService;
    }

    @GetMapping("/alerts")
    public String alerts(Model model) {

        model.addAttribute(
                "alerts",
                alertService.findAll());

        model.addAttribute(
                "activeAlerts",
                alertService.findActive());

        return "alerts";
    }

    @PostMapping("/alerts/{id}/resolve")
    public String resolve(
            @PathVariable Long id) {

        alertService.resolve(id);

        return "redirect:/alerts";
    }

    
}