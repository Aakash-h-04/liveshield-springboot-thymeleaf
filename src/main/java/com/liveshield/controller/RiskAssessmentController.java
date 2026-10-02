package com.liveshield.controller;

import com.liveshield.entity.RiskAssessment;
import com.liveshield.service.RiskAssessmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RiskAssessmentController {

    private final RiskAssessmentService service;

    public RiskAssessmentController(
            RiskAssessmentService service) {

        this.service = service;
    }

    @GetMapping("/risk-assessment")
    public String assessment(
            @RequestParam Long farmId,
            Model model) {

        RiskAssessment assessment = service.getLatest(farmId);

        model.addAttribute(
                "assessment",
                assessment);

        model.addAttribute(
                "farm",
                assessment.getFarm());

        model.addAttribute(
                "priorityActions",
                service.getPriorityActions(assessment));

        return "risk-assessment";
    }

    @PostMapping("/risk-assessment")
    public String save(
            @RequestParam Long farmId,
            @ModelAttribute RiskAssessment submitted) {

        RiskAssessment assessment = service.getLatest(farmId);

        assessment.setPerimeterControl(
                submitted.getPerimeterControl());

        assessment.setVisitorControl(
                submitted.getVisitorControl());

        assessment.setCleaningDisinfection(
                submitted.getCleaningDisinfection());

        assessment.setEquipmentHygiene(
                submitted.getEquipmentHygiene());

        assessment.setAnimalHealthMonitoring(
                submitted.getAnimalHealthMonitoring());

        assessment.setSickAnimalIsolation(
                submitted.getSickAnimalIsolation());

        assessment.setFeedStorage(
                submitted.getFeedStorage());

        assessment.setWaterSafety(
                submitted.getWaterSafety());

        assessment.setWasteManagement(
                submitted.getWasteManagement());

        service.save(assessment);

        return "redirect:/risk-assessment?farmId=" + farmId;
    }
}