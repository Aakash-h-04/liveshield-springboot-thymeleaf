package com.liveshield.service;

import com.liveshield.entity.Farm;
import com.liveshield.entity.RiskAssessment;
import com.liveshield.repository.RiskAssessmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RiskAssessmentService {

    private final RiskAssessmentRepository repository;
    private final FarmService farmService;
    @SuppressWarnings({ "unused", "resource" })
    private final AlertService alertService;

    public RiskAssessmentService(
            RiskAssessmentRepository repository,
            FarmService farmService, AlertService alertService) {

        this.repository = repository;
        this.farmService = farmService;
        this.alertService = alertService;
    }

    public RiskAssessment getLatest(Long farmId) {

        return repository
                .findTopByFarmIdOrderByAssessedAtDesc(farmId)
                .orElseGet(() -> {

                    RiskAssessment assessment = new RiskAssessment();

                    Farm farm = farmService.findById(farmId);

                    assessment.setFarm(farm);

                    return assessment;
                });
    }

    @Transactional
    public RiskAssessment save(RiskAssessment assessment) {

        Long farmId = assessment.getFarm().getId();

        Farm farm = farmService.findById(farmId);

        assessment.setFarm(farm);

        int score = calculateRiskScore(assessment);

        assessment.setRiskScore(score);
        assessment.setRiskLevel(determineRiskLevel(score));

        RiskAssessment saved = repository.save(assessment);

        updateFarmRisk(saved);

        alertService.createRiskAlert(saved);

        return saved;
    }

    private int calculateRiskScore(
            RiskAssessment assessment) {

        int total = 0;

        total += riskValue(
                assessment.getPerimeterControl(),
                15);

        total += riskValue(
                assessment.getVisitorControl(),
                10);

        total += riskValue(
                assessment.getCleaningDisinfection(),
                15);

        total += riskValue(
                assessment.getEquipmentHygiene(),
                10);

        total += riskValue(
                assessment.getAnimalHealthMonitoring(),
                15);

        total += riskValue(
                assessment.getSickAnimalIsolation(),
                10);

        total += riskValue(
                assessment.getFeedStorage(),
                10);

        total += riskValue(
                assessment.getWaterSafety(),
                5);

        total += riskValue(
                assessment.getWasteManagement(),
                10);

        return total;
    }

    private int riskValue(
            String answer,
            int weight) {

        if (answer == null || answer.isBlank()) {
            return weight;
        }

        return switch (answer) {

            case "YES" -> 0;

            case "PARTIAL" ->
                Math.round(weight * 0.5f);

            case "NO" -> weight;

            default -> weight;
        };
    }

    private String determineRiskLevel(
            int score) {

        if (score >= 60) {
            return "HIGH";
        }

        if (score >= 30) {
            return "MEDIUM";
        }

        return "LOW";
    }

    private void updateFarmRisk(
            RiskAssessment assessment) {

        Farm farm = assessment.getFarm();

        farm.setRiskLevel(
                assessment.getRiskLevel());

        farmService.save(farm);
    }

    public java.util.List<PriorityAction> getPriorityActions(
            RiskAssessment assessment) {

        java.util.List<PriorityAction> actions = new java.util.ArrayList<>();

        addAction(
                actions,
                assessment.getPerimeterControl(),
                "Improve perimeter and access control",
                "Strengthen farm boundaries and control entry into the farm.",
                15);

        addAction(
                actions,
                assessment.getVisitorControl(),
                "Strengthen visitor control",
                "Maintain controlled visitor entry and movement records.",
                10);

        addAction(
                actions,
                assessment.getCleaningDisinfection(),
                "Improve cleaning and disinfection",
                "Strengthen the regular cleaning and disinfection routine.",
                15);

        addAction(
                actions,
                assessment.getEquipmentHygiene(),
                "Improve equipment hygiene",
                "Clean and disinfect shared equipment between uses.",
                10);

        addAction(
                actions,
                assessment.getAnimalHealthMonitoring(),
                "Strengthen animal health monitoring",
                "Monitor livestock regularly and identify health changes early.",
                15);

        addAction(
                actions,
                assessment.getSickAnimalIsolation(),
                "Improve sick-animal isolation",
                "Separate sick animals from healthy stock and maintain isolation procedures.",
                10);

        addAction(
                actions,
                assessment.getFeedStorage(),
                "Protect feed storage",
                "Keep feed protected from contamination, pests and unauthorized access.",
                10);

        addAction(
                actions,
                assessment.getWaterSafety(),
                "Improve water-source protection",
                "Protect the farm water source and maintain safe water handling.",
                5);

        addAction(
                actions,
                assessment.getWasteManagement(),
                "Improve waste management",
                "Dispose of farm waste safely and maintain the waste-handling area.",
                10);

        actions.sort(
                java.util.Comparator
                        .comparingInt(PriorityAction::riskPoints)
                        .reversed());

        return actions;
    }

    private void addAction(
            java.util.List<PriorityAction> actions,
            String answer,
            String title,
            String description,
            int weight) {

        if ("YES".equals(answer)) {
            return;
        }

        String priority;

        if ("NO".equals(answer)) {
            priority = weight >= 15
                    ? "HIGH"
                    : "MEDIUM";
        } else {
            priority = weight >= 15
                    ? "MEDIUM"
                    : "LOW";
        }

        int riskPoints;

        if ("NO".equals(answer)) {
            riskPoints = weight;
        } else {
            riskPoints = Math.round(weight * 0.5f);
        }

        actions.add(
                new PriorityAction(
                        priority,
                        title,
                        description,
                        riskPoints));
    }

}