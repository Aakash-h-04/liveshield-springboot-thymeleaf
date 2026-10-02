package com.liveshield.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(
    name = "daily_logs",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"farm_id", "log_date"})
    }
)
public class DailyLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "farm_id", nullable = false)
    private Farm farm;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    private boolean footbathChecked;
    private boolean equipmentCleaned;
    private boolean visitorRecordsVerified;
    private boolean waterSourceChecked;
    private boolean feedStorageChecked;
    private boolean wasteDisposedProperly;
    private boolean sickAnimalsSeparated;
    private boolean protectiveEquipmentUsed;

    @Column(nullable = false)
    private Integer compliancePercent = 0;

    @PrePersist
    @PreUpdate
    public void calculateCompliance() {
        int completed = 0;

        if (footbathChecked) completed++;
        if (equipmentCleaned) completed++;
        if (visitorRecordsVerified) completed++;
        if (waterSourceChecked) completed++;
        if (feedStorageChecked) completed++;
        if (wasteDisposedProperly) completed++;
        if (sickAnimalsSeparated) completed++;
        if (protectiveEquipmentUsed) completed++;

        compliancePercent = (completed * 100) / 8;
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

    public LocalDate getLogDate() {
        return logDate;
    }

    public void setLogDate(LocalDate logDate) {
        this.logDate = logDate;
    }

    public boolean isFootbathChecked() {
        return footbathChecked;
    }

    public void setFootbathChecked(boolean footbathChecked) {
        this.footbathChecked = footbathChecked;
    }

    public boolean isEquipmentCleaned() {
        return equipmentCleaned;
    }

    public void setEquipmentCleaned(boolean equipmentCleaned) {
        this.equipmentCleaned = equipmentCleaned;
    }

    public boolean isVisitorRecordsVerified() {
        return visitorRecordsVerified;
    }

    public void setVisitorRecordsVerified(boolean visitorRecordsVerified) {
        this.visitorRecordsVerified = visitorRecordsVerified;
    }

    public boolean isWaterSourceChecked() {
        return waterSourceChecked;
    }

    public void setWaterSourceChecked(boolean waterSourceChecked) {
        this.waterSourceChecked = waterSourceChecked;
    }

    public boolean isFeedStorageChecked() {
        return feedStorageChecked;
    }

    public void setFeedStorageChecked(boolean feedStorageChecked) {
        this.feedStorageChecked = feedStorageChecked;
    }

    public boolean isWasteDisposedProperly() {
        return wasteDisposedProperly;
    }

    public void setWasteDisposedProperly(boolean wasteDisposedProperly) {
        this.wasteDisposedProperly = wasteDisposedProperly;
    }

    public boolean isSickAnimalsSeparated() {
        return sickAnimalsSeparated;
    }

    public void setSickAnimalsSeparated(boolean sickAnimalsSeparated) {
        this.sickAnimalsSeparated = sickAnimalsSeparated;
    }

    public boolean isProtectiveEquipmentUsed() {
        return protectiveEquipmentUsed;
    }

    public void setProtectiveEquipmentUsed(boolean protectiveEquipmentUsed) {
        this.protectiveEquipmentUsed = protectiveEquipmentUsed;
    }

    public Integer getCompliancePercent() {
        return compliancePercent;
    }
}