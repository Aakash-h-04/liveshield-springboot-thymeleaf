package com.liveshield.controller;

import com.liveshield.entity.VeterinaryRecord;
import com.liveshield.service.FarmService;
import com.liveshield.service.LivestockBatchService;
import com.liveshield.service.VeterinaryRecordService;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class VeterinaryRecordController {

    private final VeterinaryRecordService recordService;
    private final FarmService farmService;
    private final LivestockBatchService batchService;

    public VeterinaryRecordController(
            VeterinaryRecordService recordService,
            FarmService farmService,
            LivestockBatchService batchService) {

        this.recordService = recordService;
        this.farmService = farmService;
        this.batchService = batchService;
    }

    @GetMapping("/veterinary")
    public String veterinary(
            @RequestParam Long farmId,
            Model model) {

        model.addAttribute(
                "farm",
                farmService.findById(farmId));

        model.addAttribute(
                "records",
                recordService.findByFarm(farmId));

        return "veterinary";
    }

    @GetMapping("/veterinary/new")
    public String newRecord(
            @RequestParam Long farmId,
            Model model) {

        model.addAttribute(
                "farm",
                farmService.findById(farmId));

        model.addAttribute(
                "batches",
                batchService.findByFarm(farmId));

        model.addAttribute(
                "record",
                new VeterinaryRecord());

        return "veterinary-form";
    }

    @PostMapping("/veterinary")
    public String create(
            @RequestParam Long farmId,
            @RequestParam Long batchId,
            @Valid @ModelAttribute("record") VeterinaryRecord record,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("farm", farmService.findById(farmId));
            model.addAttribute("batches", batchService.findByFarm(farmId));
            return "veterinary-form";
        }

        recordService.create(farmId, batchId, record);

        return "redirect:/veterinary?farmId=" + farmId;
    }

    @PostMapping("/veterinary/{recordId}/delete")
    public String delete(
            @RequestParam Long farmId,
            @RequestParam Long batchId,
            @PathVariable Long recordId) {

        recordService.delete(
                farmId,
                batchId,
                recordId);

        return "redirect:/veterinary?farmId=" + farmId;
    }
}