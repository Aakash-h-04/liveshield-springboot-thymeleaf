package com.liveshield.service;

import com.liveshield.entity.Visitor;
import com.liveshield.entity.VisitorBiosecurityProfile;
import com.liveshield.entity.VisitorLicence;
import com.liveshield.repository.FarmVisitRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class VisitorVerificationService {

    /*
     * Evaluate whether a visitor can proceed
     * to farm check-in.
     */

    private final FarmVisitRepository farmVisitRepository;

    public VisitorVerificationService(FarmVisitRepository farmVisitRepository) {
        this.farmVisitRepository = farmVisitRepository;
    }

    public VerificationResult verify(
            VisitorService.VisitorProfile profile) {

        Visitor visitor = profile.visitor();
        VisitorLicence licence = profile.licence();
        VisitorBiosecurityProfile biosecurity = profile.biosecurityProfile();

        List<String> reasons = new ArrayList<>();

        /*
         * ------------------------------------------------
         * 1. ACCOUNT STATUS
         * ------------------------------------------------
         */

        if (!"ACTIVE".equalsIgnoreCase(
                visitor.getAccountStatus())) {

            return new VerificationResult(
                    "DENIED",
                    "Visitor account is not active.",
                    List.of(
                            "Visitor account status: "
                                    + visitor.getAccountStatus()));
        }

        /*
         * ------------------------------------------------
         * 2. LICENCE
         * ------------------------------------------------
         */

        if (licence == null) {

            return new VerificationResult(
                    "DENIED",
                    "No professional licence is registered.",
                    List.of(
                            "A valid visitor licence is required."));
        }

        if (!"VERIFIED".equalsIgnoreCase(
                licence.getVerificationStatus())) {

            return new VerificationResult(
                    "DENIED",
                    "Visitor licence has not been verified.",
                    List.of(
                            "Licence status: "
                                    + licence.getVerificationStatus()));
        }

        /*
         * ------------------------------------------------
         * 3. BIOSECURITY PROFILE
         * ------------------------------------------------
         */

        if (biosecurity == null) {

            return new VerificationResult(
                    "REVIEW_REQUIRED",
                    "Biosecurity profile is incomplete.",
                    List.of(
                            "Visitor biosecurity profile "
                                    + "requires verification."));
        }

        if (!"VERIFIED".equalsIgnoreCase(
                biosecurity.getBiosecurityStatus())) {

            return new VerificationResult(
                    "REVIEW_REQUIRED",
                    "Visitor biosecurity profile requires verification.",
                    List.of(
                            "Biosecurity status: "
                                    + biosecurity.getBiosecurityStatus()));
        }

        /*
         * ------------------------------------------------
         * 4. LICENCE EXPIRY
         * ------------------------------------------------
         */

        if (licence.getExpiryDate() != null
                && licence.getExpiryDate()
                        .isBefore(LocalDate.now())) {

            return new VerificationResult(
                    "DENIED",
                    "Visitor licence has expired.",
                    List.of(
                            "Licence expired on: "
                                    + licence.getExpiryDate()));
        }

        /*
         * ------------------------------------------------
         * 5. RECENT SICK-ANIMAL CONTACT
         * ------------------------------------------------
         */

        if (biosecurity.isRecentSickAnimalContact()) {

            reasons.add(
                    "Recent contact with a sick animal was declared.");
        }

        /*
         * ------------------------------------------------
         * 6. RECENT POULTRY CONTACT
         * ------------------------------------------------
         */

        if (biosecurity.isRecentPoultryContact()) {

            reasons.add(
                    "Recent poultry-farm contact was declared.");
        }

        /*
         * ------------------------------------------------
         * 7. RECENT LIVESTOCK CONTACT
         * ------------------------------------------------
         */

        if (biosecurity.isRecentLivestockContact()) {

            reasons.add(
                    "Recent livestock contact was declared.");
        }

        /*
         * ------------------------------------------------
         * 8. HEALTH DECLARATION
         * ------------------------------------------------
         */

        if ("DECLARED_SYMPTOMS".equalsIgnoreCase(
                biosecurity.getHealthDeclaration())) {

            reasons.add(
                    "Visitor has declared relevant symptoms.");
        }

        /*
         * ------------------------------------------------
         * 9. PROFILE STATUS
         * ------------------------------------------------
         */

        if ("RESTRICTED".equalsIgnoreCase(
                biosecurity.getBiosecurityStatus())) {

            return new VerificationResult(
                    "DENIED",
                    "Visitor biosecurity profile is restricted.",
                    reasons);
        }

        /*
         * ------------------------------------------------
         * 10. FINAL DECISION
         * ------------------------------------------------
         */

        if (!reasons.isEmpty()) {

            return new VerificationResult(
                    "REVIEW_REQUIRED",
                    "Additional biosecurity review is required.",
                    reasons);
        }

        /*
         * Everything required for basic verification
         * is currently satisfied.
         */

        return new VerificationResult(
                "ALLOWED",
                "Visitor passed the current biosecurity checks.",
                List.of());
    }

    /*
     * Result returned by the verification engine.
     */
    public record VerificationResult(
            String decision,
            String message,
            List<String> reasons) {
    }
}