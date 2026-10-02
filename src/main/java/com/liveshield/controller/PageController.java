package com.liveshield.controller;

import com.liveshield.entity.Farm;
import com.liveshield.service.AlertService;
import com.liveshield.service.FarmService;
import com.liveshield.service.LivestockBatchService;
import com.liveshield.service.VeterinaryRecordService;
import com.liveshield.entity.VeterinaryRecord;
import com.liveshield.service.VisitorService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
public class PageController {

    private final FarmService farmService;
    private final AlertService alertService;
    private final LivestockBatchService batchService;
    private final VeterinaryRecordService veterinaryService;
    private final VisitorService visitorService;

    public PageController(
            FarmService farmService,
            AlertService alertService,
            LivestockBatchService batchService,
            VeterinaryRecordService veterinaryService,
            VisitorService visitorService) {

        this.farmService = farmService;
        this.alertService = alertService;
        this.batchService = batchService;
        this.veterinaryService = veterinaryService;
        this.visitorService = visitorService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {

        var farms = farmService.findAll();

        /*
         * Farm metrics
         */
        model.addAttribute("farms", farms);

        model.addAttribute(
                "farmCount",
                farms.size());

        model.addAttribute(
                "livestockTotal",
                farms.stream()
                        .mapToInt(f -> f.getLivestockCount() == null
                                ? 0
                                : f.getLivestockCount())
                        .sum());

        model.addAttribute(
                "averageCompliance",
                farms.isEmpty()
                        ? 0
                        : Math.round(
                                farms.stream()
                                        .mapToInt(f -> f.getCompliancePercent() == null
                                                ? 0
                                                : f.getCompliancePercent())
                                        .average()
                                        .orElse(0)));

        model.addAttribute(
                "highRiskCount",
                farms.stream()
                        .filter(f -> "HIGH".equalsIgnoreCase(
                                f.getRiskLevel()))
                        .count());

        /*
         * Alert information
         */

        var activeAlerts = alertService.findActive();

        model.addAttribute(
                "activeAlerts",
                activeAlerts);

        model.addAttribute(
                "activeAlertCount",
                activeAlerts.size());

        /*
         * Recent alerts
         */

        var recentAlerts = alertService.findAll()
                .stream()
                .limit(5)
                .toList();

        model.addAttribute(
                "recentAlerts",
                recentAlerts);

        /*
         * Livestock batch information
         */

        int sickBatchCount = 0;

        for (Farm farm : farms) {

            sickBatchCount += batchService.findByFarm(farm.getId())
                    .stream()
                    .filter(batch -> "SICK".equalsIgnoreCase(
                            batch.getHealthStatus()))
                    .count();
        }

        model.addAttribute(
                "sickBatchCount",
                sickBatchCount);

        /*
         * Recent veterinary records
         */

        List<VeterinaryRecord> recentVeterinaryRecords = new ArrayList<>();

        for (Farm farm : farms) {

            recentVeterinaryRecords.addAll(
                    veterinaryService.findByFarm(
                            farm.getId()));
        }

        recentVeterinaryRecords.sort(
                Comparator.comparing(
                        VeterinaryRecord::getRecordDate,
                        Comparator.nullsLast(
                                Comparator.reverseOrder())));

        model.addAttribute(
                "recentVeterinaryRecords",
                recentVeterinaryRecords
                        .stream()
                        .limit(5)
                        .toList());

        model.addAttribute(
                "activeVisitorCount",
                visitorService.getActiveVisitorCount());

        model.addAttribute(
                "todayCheckInCount",
                visitorService.getTodayCheckInCount());

        model.addAttribute(
                "todayCheckOutCount",
                visitorService.getTodayCheckOutCount());

        return "dashboard";
    }

    @GetMapping("/farms/new")
    public String newFarm(Model model) {

        model.addAttribute(
                "farm",
                new Farm());

        return "farm-form";
    }

    @PostMapping("/farms")
    public String createFarm(
            @Valid @ModelAttribute("farm") Farm farm,
            BindingResult result) {

        if (result.hasErrors()) {
            return "farm-form";
        }
        farmService.save(farm);

        return "redirect:/";
    }
}