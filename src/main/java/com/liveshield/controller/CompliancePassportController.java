package com.liveshield.controller;

import com.liveshield.service.CompliancePassportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CompliancePassportController {

    private final CompliancePassportService passportService;

    public CompliancePassportController(
            CompliancePassportService passportService) {

        this.passportService = passportService;
    }

    @GetMapping("/compliance-passport")
    public String passport(
            @RequestParam Long farmId,
            Model model) {

        model.addAttribute(
                "passport",
                passportService.getPassport(farmId)
        );

        return "compliance-passport";
    }
}