package com.liveshield.service;

import com.liveshield.entity.Farm;
import com.liveshield.entity.RiskAssessment;
import org.springframework.stereotype.Service;

@Service
public class CompliancePassportService {

    private final FarmService farmService;
    private final RiskAssessmentService riskAssessmentService;

    public CompliancePassportService(
            FarmService farmService,
            RiskAssessmentService riskAssessmentService) {

        this.farmService = farmService;
        this.riskAssessmentService = riskAssessmentService;
    }

    public CompliancePassport getPassport(Long farmId) {

        Farm farm = farmService.findById(farmId);

        RiskAssessment assessment =
                riskAssessmentService.getLatest(farmId);

        String passportStatus =
                determinePassportStatus(
                        farm.getCompliancePercent(),
                        farm.getRiskLevel()
                );

        return new CompliancePassport(
                farm,
                assessment,
                passportStatus
        );
    }

    private String determinePassportStatus(
            Integer compliancePercent,
            String riskLevel) {

        if ("HIGH".equals(riskLevel)
                || compliancePercent < 40) {

            return "ATTENTION REQUIRED";
        }

        if ("MEDIUM".equals(riskLevel)
                || compliancePercent <= 70) {

            return "UNDER REVIEW";
        }

        return "ACTIVE";
    }

    public record CompliancePassport(
            Farm farm,
            RiskAssessment assessment,
            String passportStatus
    ) {
    }
}