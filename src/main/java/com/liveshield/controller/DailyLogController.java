package com.liveshield.controller;

import com.liveshield.entity.DailyLog;
import com.liveshield.service.DailyLogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class DailyLogController {

    private final DailyLogService dailyLogService;

    public DailyLogController(DailyLogService dailyLogService) {
        this.dailyLogService = dailyLogService;
    }

    @GetMapping("/daily-log")
    public String dailyLog(
            @RequestParam Long farmId,
            Model model) {

        DailyLog log = dailyLogService.getTodayLog(farmId);

        model.addAttribute("log", log);
        model.addAttribute("farm", log.getFarm());

        return "daily-log";
    }

    @PostMapping("/daily-log")
    public String saveDailyLog(
            @RequestParam Long farmId,
            @ModelAttribute DailyLog submittedLog) {

        /*
         * Always update today's existing log instead of
         * creating a new DailyLog from the form submission.
         */
        DailyLog log = dailyLogService.getTodayLog(farmId);

        log.setFootbathChecked(submittedLog.isFootbathChecked());
        log.setEquipmentCleaned(submittedLog.isEquipmentCleaned());
        log.setVisitorRecordsVerified(
                submittedLog.isVisitorRecordsVerified()
        );
        log.setWaterSourceChecked(
                submittedLog.isWaterSourceChecked()
        );
        log.setFeedStorageChecked(
                submittedLog.isFeedStorageChecked()
        );
        log.setWasteDisposedProperly(
                submittedLog.isWasteDisposedProperly()
        );
        log.setSickAnimalsSeparated(
                submittedLog.isSickAnimalsSeparated()
        );
        log.setProtectiveEquipmentUsed(
                submittedLog.isProtectiveEquipmentUsed()
        );

        dailyLogService.save(log);

        return "redirect:/daily-log?farmId=" + farmId;
    }
}