package com.liveshield.service;

import com.liveshield.entity.Farm;
import com.liveshield.entity.FarmVisit;
import com.liveshield.entity.Visitor;
import com.liveshield.repository.FarmVisitRepository;
import com.liveshield.repository.VisitorRepository;
import com.liveshield.service.VisitorVerificationService.VerificationResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class VisitorCheckInService {

    private final VisitorRepository visitorRepository;
    private final FarmVisitRepository farmVisitRepository;
    private final VisitorService visitorService;
    private final VisitorVerificationService verificationService;

    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private final SecureRandom secureRandom = new SecureRandom();

    public VisitorCheckInService(
            VisitorRepository visitorRepository,
            FarmVisitRepository farmVisitRepository,
            VisitorService visitorService,
            VisitorVerificationService verificationService) {

        this.visitorRepository = visitorRepository;
        this.farmVisitRepository = farmVisitRepository;
        this.visitorService = visitorService;
        this.verificationService = verificationService;
    }

    @Transactional
    public FarmVisit checkIn(
            String visitorCode,
            Farm farm,
            String purpose,
            String vehicleNumber) {

        Visitor visitor = visitorRepository
                .findByVisitorCode(visitorCode)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Visitor not found."));

        if (farm == null) {
            throw new IllegalArgumentException(
                    "Destination farm is required.");
        }

        if (purpose == null || purpose.isBlank()) {
            throw new IllegalArgumentException(
                    "Purpose of visit is required.");
        }

        /*
         * Re-run visitor verification at the moment
         * of farm entry.
         */
        VisitorService.VisitorProfile profile = visitorService.loadProfile(visitorCode);

        VerificationResult verification = verificationService.verify(profile);

        if (!"ALLOWED".equalsIgnoreCase(
                verification.decision())) {

            throw new IllegalArgumentException(
                    "Farm entry is not permitted: "
                            + verification.message());
        }

        /*
         * Prevent multiple active visits for the
         * same visitor.
         */
        if (farmVisitRepository
                .findTopByVisitorIdOrderByCheckInTimeDesc(
                        visitor.getId())
                .map(visit -> visit.getCheckOutTime() == null)
                .orElse(false)) {

            throw new IllegalArgumentException(
                    "Visitor already has an active farm visit.");
        }

        FarmVisit visit = new FarmVisit();

        visit.setVisitor(visitor);
        visit.setFarm(farm);
        visit.setPurpose(purpose.trim());

        if (vehicleNumber != null
                && !vehicleNumber.isBlank()) {

            visit.setVehicleNumber(
                    vehicleNumber.trim());
        }

        visit.setCheckInTime(LocalDateTime.now());

        visit.setVerificationStatus("VERIFIED");
        visit.setEntryDecision("ALLOWED");
        visit.setVerifiedBy("SYSTEM");
        visit.setVerificationRemarks(
                "Visitor passed biosecurity verification.");

        String passCode = generatePassCode();

        visit.setPassCode(passCode);
        visit.setPassIssuedAt(LocalDateTime.now());

        /*
         * Initial e-pass validity:
         * 8 hours from check-in.
         */
        visit.setPassValidUntil(
                LocalDateTime.now().plusHours(8));

        return farmVisitRepository.save(visit);
    }

    @Transactional
    public FarmVisit checkOut(Long visitId) {

        FarmVisit visit = farmVisitRepository
                .findById(visitId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Farm visit not found."));

        if (visit.getCheckOutTime() != null) {
            throw new IllegalArgumentException(
                    "Visitor has already been checked out.");
        }

        visit.setCheckOutTime(LocalDateTime.now());

        return farmVisitRepository.save(visit);
    }

    private String generatePassCode() {

        String code;

        do {
            code = "PASS-"
                    + randomPart(4)
                    + "-"
                    + randomPart(4)
                    + "-"
                    + randomPart(4);

        } while (farmVisitRepository
                .findByPassCode(code)
                .isPresent());

        return code;
    }

    private String randomPart(int length) {

        StringBuilder result = new StringBuilder(length);

        for (int i = 0; i < length; i++) {

            int index = secureRandom.nextInt(
                    CODE_CHARS.length());

            result.append(
                    CODE_CHARS.charAt(index));
        }

        return result.toString();
    }
}