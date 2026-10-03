package com.liveshield.service;

import com.liveshield.entity.Visitor;
import com.liveshield.entity.VisitorBiosecurityProfile;
import com.liveshield.entity.VisitorLicence;
import com.liveshield.entity.FarmVisit;
import com.liveshield.repository.FarmVisitRepository;
import com.liveshield.repository.VisitorBiosecurityProfileRepository;
import com.liveshield.repository.VisitorLicenceRepository;
import com.liveshield.repository.VisitorRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class VisitorService {

    private final VisitorRepository visitorRepository;
    private final VisitorLicenceRepository licenceRepository;
    private final VisitorBiosecurityProfileRepository biosecurityRepository;
    private final FarmVisitRepository farmVisitRepository;

    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private final SecureRandom secureRandom = new SecureRandom();

    public VisitorService(
            VisitorRepository visitorRepository,
            VisitorLicenceRepository licenceRepository,
            VisitorBiosecurityProfileRepository biosecurityRepository,
            FarmVisitRepository farmVisitRepository) {

        this.visitorRepository = visitorRepository;
        this.licenceRepository = licenceRepository;
        this.biosecurityRepository = biosecurityRepository;
        this.farmVisitRepository = farmVisitRepository;
    }

    /*
     * Register a completely new visitor.
     */
    @Transactional
    public Visitor registerVisitor(
            Visitor visitor,
            VisitorLicence licence) {

        if (visitorRepository.findByMobile(visitor.getMobile()).isPresent()) {
            throw new IllegalArgumentException(
                    "A visitor with this mobile number already exists.");
        }

        visitor.setVisitorCode(generateVisitorCode());
        visitor.setAccountStatus("ACTIVE");

        Visitor savedVisitor = visitorRepository.save(visitor);

        /*
         * Every visitor starts with a biosecurity profile.
         * It must be verified before unrestricted entry.
         */
        VisitorBiosecurityProfile profile = new VisitorBiosecurityProfile();

        profile.setVisitor(savedVisitor);
        profile.setBiosecurityStatus("REVIEW_REQUIRED");
        profile.setHealthDeclaration("NOT_PROVIDED");

        biosecurityRepository.save(profile);

        /*
         * Licence is initially PENDING.
         * We never trust a visitor's declaration
         * as automatic verification.
         */
        if (licence != null) {

            licence.setVisitor(savedVisitor);
            licence.setVerificationStatus("PENDING");

            licenceRepository.save(licence);
        }

        return savedVisitor;
    }

    /*
     * Find visitor using the permanent identity
     * encoded in the QR/barcode.
     */
    @Transactional(readOnly = true)
    public Optional<Visitor> findByVisitorCode(
            String visitorCode) {

        return visitorRepository.findByVisitorCode(visitorCode);
    }

    /*
     * Find visitor using mobile number.
     */
    @Transactional(readOnly = true)
    public Optional<Visitor> findByMobile(
            String mobile) {

        return visitorRepository.findByMobile(mobile);
    }

    /*
     * Load the complete profile associated with
     * the permanent visitor identity.
     */
    @Transactional(readOnly = true)
    public VisitorProfile loadProfile(
            String visitorCode) {

        Visitor visitor = visitorRepository
                .findByVisitorCode(visitorCode)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Visitor not found."));

        Optional<VisitorLicence> licence = licenceRepository
                .findTopByVisitorIdOrderByExpiryDateDesc(
                        visitor.getId());

        Optional<VisitorBiosecurityProfile> biosecurity = biosecurityRepository
                .findByVisitorId(visitor.getId());

        return new VisitorProfile(
                visitor,
                licence.orElse(null),
                biosecurity.orElse(null));
    }

    @Transactional(readOnly = true)
    public long getActiveVisitorCount() {

        return farmVisitRepository
                .countByCheckOutTimeIsNull();
    }

    @Transactional(readOnly = true)
    public List<Visitor> getRegisteredActiveVisitors() {

        return visitorRepository
                .findByAccountStatusOrderByFullNameAsc("ACTIVE");
    }

    @Transactional(readOnly = true)
    public long getTodayCheckInCount() {

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();

        return farmVisitRepository
                .countByCheckInTimeGreaterThanEqual(
                        startOfDay);
    }

    @Transactional(readOnly = true)
    public long getTodayCheckOutCount() {

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();

        return farmVisitRepository
                .countByCheckOutTimeGreaterThanEqual(
                        startOfDay);
    }

    /*
     * Generate an opaque visitor identity.
     *
     * Example:
     * VST-7K9X-42MQ-8P2D
     */
    private String generateVisitorCode() {

        String code;

        do {
            code = "VST-"
                    + randomPart(4)
                    + "-"
                    + randomPart(4)
                    + "-"
                    + randomPart(4);

        } while (visitorRepository.existsByVisitorCode(code));

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

    /*
     * Combined visitor information used by
     * controllers and UI.
     */
    public record VisitorProfile(
            Visitor visitor,
            VisitorLicence licence,
            VisitorBiosecurityProfile biosecurityProfile) {
    }

    @Transactional
    public VisitorLicence verifyLicence(
            Long licenceId,
            String remarks) {

        VisitorLicence licence = licenceRepository.findById(licenceId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Visitor licence not found."));

        licence.setVerificationStatus("VERIFIED");
        licence.setVerifiedAt(java.time.LocalDateTime.now());
        licence.setVerificationRemarks(remarks);

        return licenceRepository.save(licence);
    }

    @Transactional
    public VisitorLicence rejectLicence(
            Long licenceId,
            String remarks) {

        VisitorLicence licence = licenceRepository.findById(licenceId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Visitor licence not found."));

        licence.setVerificationStatus("REJECTED");
        licence.setVerifiedAt(java.time.LocalDateTime.now());
        licence.setVerificationRemarks(remarks);

        return licenceRepository.save(licence);
    }

    @Transactional(readOnly = true)
    public VisitorLicence findLicenceById(Long licenceId) {

        return licenceRepository.findById(licenceId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Visitor licence not found."));
    }

    @Transactional
    public VisitorBiosecurityProfile verifyBiosecurity(
            Long visitorId,
            String remarks) {

        VisitorBiosecurityProfile profile = biosecurityRepository.findByVisitorId(visitorId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Visitor biosecurity profile not found."));

        if ("RESTRICTED".equalsIgnoreCase(
                profile.getBiosecurityStatus())) {

            throw new IllegalArgumentException(
                    "A restricted biosecurity profile cannot be verified.");
        }

        profile.setBiosecurityStatus("VERIFIED");
        profile.setLastVerifiedAt(
                java.time.LocalDateTime.now());
        profile.setVerificationRemarks(remarks);

        return biosecurityRepository.save(profile);
    }

    @Transactional(readOnly = true)
    public List<FarmVisit> getVisitHistory(
            Long visitorId) {

        return farmVisitRepository
                .findByVisitorIdOrderByCheckInTimeDesc(visitorId);
    }

    @Transactional(readOnly = true)
    public List<FarmVisit> getRecentFarmVisits(
            Long visitorId,
            int limit) {

        List<FarmVisit> visits = farmVisitRepository
                .findByVisitorIdOrderByCheckInTimeDesc(
                        visitorId);

        if (visits.size() <= limit) {
            return visits;
        }

        return visits.subList(0, limit);
    }
}