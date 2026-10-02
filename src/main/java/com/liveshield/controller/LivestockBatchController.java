package com.liveshield.controller;

import com.liveshield.entity.LivestockBatch;
import com.liveshield.service.FarmService;
import com.liveshield.service.LivestockBatchService;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LivestockBatchController {

    private final LivestockBatchService batchService;
    private final FarmService farmService;

    public LivestockBatchController(
            LivestockBatchService batchService,
            FarmService farmService) {

        this.batchService = batchService;
        this.farmService = farmService;
    }

    @GetMapping("/livestock")
    public String livestock(
            @RequestParam Long farmId,
            Model model) {

        model.addAttribute(
                "farm",
                farmService.findById(farmId));

        model.addAttribute(
                "batches",
                batchService.findByFarm(farmId));

        return "livestock";
    }

    @GetMapping("/livestock/new")
    public String newBatch(
            @RequestParam Long farmId,
            Model model) {

        model.addAttribute(
                "farm",
                farmService.findById(farmId));

        model.addAttribute(
                "batch",
                new LivestockBatch());

        return "livestock-form";
    }

    @PostMapping("/livestock")
    public String create(
            @RequestParam Long farmId,
            @Valid @ModelAttribute("batch") LivestockBatch batch,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("farm", farmService.findById(farmId));
            return "livestock-form";
        }

        batchService.create(farmId, batch);

        return "redirect:/livestock?farmId=" + farmId;
    }

    @GetMapping("/livestock/{id}/edit")
    public String edit(
            @RequestParam Long farmId,
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "farm",
                farmService.findById(farmId));

        model.addAttribute(
                "batch",
                batchService.get(farmId, id));

        return "livestock-form";
    }

    @PostMapping("/livestock/{id}")
    public String update(
            @RequestParam Long farmId,
            @PathVariable Long id,
            @Valid @ModelAttribute("batch") LivestockBatch batch,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("farm", farmService.findById(farmId));
            return "livestock-form";
        }

        batchService.update(farmId, id, batch);

        return "redirect:/livestock?farmId=" + farmId;
    }

    @PostMapping("/livestock/{id}/delete")
    public String delete(
            @RequestParam Long farmId,
            @PathVariable Long id) {

        batchService.delete(farmId, id);

        return "redirect:/livestock?farmId=" + farmId;
    }
}