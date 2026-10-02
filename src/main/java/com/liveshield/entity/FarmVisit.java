package com.liveshield.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "farm_visits")
public class FarmVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Visitor making this visit.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "visitor_id", nullable = false)
    private Visitor visitor;

    /*
     * Farm being visited.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "farm_id", nullable = false)
    private Farm farm;

    /*
     * Example:
     * Feed delivery
     * Veterinary service
     * Equipment maintenance
     * Egg collection
     * Livestock transport
     * Inspection
     */
    private String purpose;

    /*
     * Vehicle associated with the visit, if applicable.
     */
    private String vehicleNumber;

    /*
     * Entry / exit timestamps.
     */
    @Column(nullable = false)
    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    /*
     * Verification state.
     *
     * PENDING
     * VERIFIED
     * REQUIRES_REVIEW
     * REJECTED
     */
    @Column(nullable = false)
    private String verificationStatus = "PENDING";

    /*
     * Final entry decision.
     *
     * ALLOWED
     * REVIEW_REQUIRED
     * DENIED
     */
    @Column(nullable = false)
    private String entryDecision = "REVIEW_REQUIRED";

    /*
     * User who performed the verification.
     * Later this can be connected to an actual
     * application user/account.
     */
    private String verifiedBy;

    private String verificationRemarks;

    /*
     * Temporary e-Pass generated for this visit.
     */
    @Column(unique = true)
    private String passCode;

    private LocalDateTime passIssuedAt;

    private LocalDateTime passValidUntil;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();

        if (checkInTime == null) {
            checkInTime = LocalDateTime.now();
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Visitor getVisitor() {
        return visitor;
    }

    public void setVisitor(Visitor visitor) {
        this.visitor = visitor;
    }

    public Farm getFarm() {
        return farm;
    }

    public void setFarm(Farm farm) {
        this.farm = farm;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LocalDateTime getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(LocalDateTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getEntryDecision() {
        return entryDecision;
    }

    public void setEntryDecision(String entryDecision) {
        this.entryDecision = entryDecision;
    }

    public String getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(String verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    public String getVerificationRemarks() {
        return verificationRemarks;
    }

    public void setVerificationRemarks(String verificationRemarks) {
        this.verificationRemarks = verificationRemarks;
    }

    public String getPassCode() {
        return passCode;
    }

    public void setPassCode(String passCode) {
        this.passCode = passCode;
    }

    public LocalDateTime getPassIssuedAt() {
        return passIssuedAt;
    }

    public void setPassIssuedAt(LocalDateTime passIssuedAt) {
        this.passIssuedAt = passIssuedAt;
    }

    public LocalDateTime getPassValidUntil() {
        return passValidUntil;
    }

    public void setPassValidUntil(LocalDateTime passValidUntil) {
        this.passValidUntil = passValidUntil;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}