package com.liveshield.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "visitor_biosecurity_profiles")
public class VisitorBiosecurityProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "visitor_id",
        nullable = false,
        unique = true
    )
    private Visitor visitor;

    /*
     * Recent contact with livestock/poultry.
     */
    @Column(nullable = false)
    private boolean recentLivestockContact = false;

    @Column(nullable = false)
    private boolean recentPoultryContact = false;

    @Column(nullable = false)
    private boolean recentSickAnimalContact = false;

    /*
     * Most recent visit to another livestock/poultry farm.
     */
    private LocalDate lastFarmVisitDate;

    /*
     * Optional details about the previous farm exposure.
     */
    private String lastFarmType;

    /*
     * Health declaration made during verification.
     *
     * CLEAR
     * DECLARED_SYMPTOMS
     * NOT_PROVIDED
     */
    @Column(nullable = false)
    private String healthDeclaration = "NOT_PROVIDED";

    /*
     * Whether the visitor declared symptoms
     * relevant to farm entry screening.
     */
    @Column(nullable = false)
    private boolean symptomsDeclared = false;

    /*
     * Overall biosecurity status of this profile.
     *
     * CLEAR
     * REVIEW_REQUIRED
     * RESTRICTED
     */
    @Column(nullable = false)
    private String biosecurityStatus = "REVIEW_REQUIRED";

    private LocalDateTime lastVerifiedAt;

    private String verificationRemarks;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
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

    public boolean isRecentLivestockContact() {
        return recentLivestockContact;
    }

    public void setRecentLivestockContact(boolean recentLivestockContact) {
        this.recentLivestockContact = recentLivestockContact;
    }

    public boolean isRecentPoultryContact() {
        return recentPoultryContact;
    }

    public void setRecentPoultryContact(boolean recentPoultryContact) {
        this.recentPoultryContact = recentPoultryContact;
    }

    public boolean isRecentSickAnimalContact() {
        return recentSickAnimalContact;
    }

    public void setRecentSickAnimalContact(boolean recentSickAnimalContact) {
        this.recentSickAnimalContact = recentSickAnimalContact;
    }

    public LocalDate getLastFarmVisitDate() {
        return lastFarmVisitDate;
    }

    public void setLastFarmVisitDate(LocalDate lastFarmVisitDate) {
        this.lastFarmVisitDate = lastFarmVisitDate;
    }

    public String getLastFarmType() {
        return lastFarmType;
    }

    public void setLastFarmType(String lastFarmType) {
        this.lastFarmType = lastFarmType;
    }

    public String getHealthDeclaration() {
        return healthDeclaration;
    }

    public void setHealthDeclaration(String healthDeclaration) {
        this.healthDeclaration = healthDeclaration;
    }

    public boolean isSymptomsDeclared() {
        return symptomsDeclared;
    }

    public void setSymptomsDeclared(boolean symptomsDeclared) {
        this.symptomsDeclared = symptomsDeclared;
    }

    public String getBiosecurityStatus() {
        return biosecurityStatus;
    }

    public void setBiosecurityStatus(String biosecurityStatus) {
        this.biosecurityStatus = biosecurityStatus;
    }

    public LocalDateTime getLastVerifiedAt() {
        return lastVerifiedAt;
    }

    public void setLastVerifiedAt(LocalDateTime lastVerifiedAt) {
        this.lastVerifiedAt = lastVerifiedAt;
    }

    public String getVerificationRemarks() {
        return verificationRemarks;
    }

    public void setVerificationRemarks(String verificationRemarks) {
        this.verificationRemarks = verificationRemarks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}