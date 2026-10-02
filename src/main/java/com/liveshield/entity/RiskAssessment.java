package com.liveshield.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "risk_assessments")
public class RiskAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "farm_id", nullable = false)
    private Farm farm;

    /*
     * Perimeter & access
     */
    private String perimeterControl;
    private String visitorControl;

    /*
     * Cleaning & sanitation
     */
    private String cleaningDisinfection;
    private String equipmentHygiene;

    /*
     * Animal health
     */
    private String animalHealthMonitoring;
    private String sickAnimalIsolation;

    /*
     * Feed & water
     */
    private String feedStorage;
    private String waterSafety;

    /*
     * Waste management
     */
    private String wasteManagement;

    /*
     * Calculated result
     */
    @Column(nullable = false)
    private Integer riskScore = 0;

    @Column(nullable = false)
    private String riskLevel = "LOW";

    @Column(nullable = false)
    private LocalDateTime assessedAt;

    @PrePersist
    @PreUpdate
    void updateTimestamp() {
        assessedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Farm getFarm() {
        return farm;
    }

    public void setFarm(Farm farm) {
        this.farm = farm;
    }

    public String getPerimeterControl() {
        return perimeterControl;
    }

    public void setPerimeterControl(String perimeterControl) {
        this.perimeterControl = perimeterControl;
    }

    public String getVisitorControl() {
        return visitorControl;
    }

    public void setVisitorControl(String visitorControl) {
        this.visitorControl = visitorControl;
    }

    public String getCleaningDisinfection() {
        return cleaningDisinfection;
    }

    public void setCleaningDisinfection(String cleaningDisinfection) {
        this.cleaningDisinfection = cleaningDisinfection;
    }

    public String getEquipmentHygiene() {
        return equipmentHygiene;
    }

    public void setEquipmentHygiene(String equipmentHygiene) {
        this.equipmentHygiene = equipmentHygiene;
    }

    public String getAnimalHealthMonitoring() {
        return animalHealthMonitoring;
    }

    public void setAnimalHealthMonitoring(String animalHealthMonitoring) {
        this.animalHealthMonitoring = animalHealthMonitoring;
    }

    public String getSickAnimalIsolation() {
        return sickAnimalIsolation;
    }

    public void setSickAnimalIsolation(String sickAnimalIsolation) {
        this.sickAnimalIsolation = sickAnimalIsolation;
    }

    public String getFeedStorage() {
        return feedStorage;
    }

    public void setFeedStorage(String feedStorage) {
        this.feedStorage = feedStorage;
    }

    public String getWaterSafety() {
        return waterSafety;
    }

    public void setWaterSafety(String waterSafety) {
        this.waterSafety = waterSafety;
    }

    public String getWasteManagement() {
        return wasteManagement;
    }

    public void setWasteManagement(String wasteManagement) {
        this.wasteManagement = wasteManagement;
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Integer riskScore) {
        this.riskScore = riskScore;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public LocalDateTime getAssessedAt() {
        return assessedAt;
    }
}