package com.liveshield.controller;

import com.liveshield.entity.Farm;
import com.liveshield.entity.FarmVisit;
import com.liveshield.entity.Visitor;
import com.liveshield.entity.VisitorLicence;
import com.liveshield.repository.FarmVisitRepository;
import com.liveshield.service.FarmService;
import com.liveshield.service.QrCodeService;
import com.liveshield.service.VisitorCheckInService;
import com.liveshield.service.VisitorService;
import com.liveshield.service.VisitorVerificationService;
import com.liveshield.entity.VisitorLicence;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/visitors")
public class VisitorController {

    private final VisitorService visitorService;
    private final FarmService farmService;
    private final QrCodeService qrCodeService;
    private final VisitorVerificationService verificationService;

    private final VisitorCheckInService checkInService;
    private final FarmVisitRepository farmVisitRepository;

    public VisitorController(
            VisitorService visitorService,
            QrCodeService qrCodeService,
            VisitorVerificationService verificationService,
            FarmService farmService,
            VisitorCheckInService checkInService,
            FarmVisitRepository farmVisitRepository) {

        this.visitorService = visitorService;
        this.qrCodeService = qrCodeService;
        this.verificationService = verificationService;
        this.farmService = farmService;
        this.checkInService = checkInService;
        this.farmVisitRepository = farmVisitRepository;
    }

    /*
     * Visitor Management dashboard.
     */
    @GetMapping
    public String visitors(Model model) {

        List<FarmVisit> activeVisits = farmVisitRepository
                .findByCheckOutTimeIsNullOrderByCheckInTimeDesc();

        List<Farm> farms = farmService.findAll();

        model.addAttribute("activeVisits", activeVisits);
        model.addAttribute("farms", farms);

        model.addAttribute(
                "registeredActiveVisitors",
                visitorService.getRegisteredActiveVisitors());

        return "visitors";
    }

    /*
     * Registration page.
     */
    @GetMapping("/register")
    public String registerForm(Model model) {

        model.addAttribute(
                "visitor",
                new Visitor());

        model.addAttribute(
                "licence",
                new VisitorLicence());

        return "visitor-register";
    }

    /*
     * Register a new visitor.
     */
    @PostMapping("/register")
    public String register(
            @ModelAttribute("visitor") Visitor visitor,

            @ModelAttribute("licence") VisitorLicence licence,

            Model model) {

        try {

            Visitor saved = visitorService.registerVisitor(
                    visitor,
                    licence);

            return "redirect:/visitors/"
                    + saved.getVisitorCode();

        } catch (IllegalArgumentException ex) {

            model.addAttribute(
                    "error",
                    ex.getMessage());

            return "visitor-register";
        }
    }

    /*
     * Search a visitor using the permanent
     * Visitor ID encoded in the QR/barcode.
     */
    @GetMapping("/lookup")
    public String lookup(
            @RequestParam String visitorCode,
            Model model) {

        try {

            VisitorService.VisitorProfile profile = visitorService.loadProfile(visitorCode);

            Visitor visitor = profile.visitor();

            List<FarmVisit> visitHistory = visitorService.getVisitHistory(visitor.getId());

            List<FarmVisit> recentFarmVisits = visitorService.getRecentFarmVisits(
                    visitor.getId(),
                    5);

            model.addAttribute("profile", profile);

            model.addAttribute(
                    "visitHistory",
                    visitHistory);

            model.addAttribute(
                    "recentFarmVisits",
                    recentFarmVisits);

            String qrCode = qrCodeService.generateBase64(
                    visitor.getVisitorCode());

            model.addAttribute(
                    "qrCode",
                    qrCode);

            return "visitor-profile";

        } catch (IllegalArgumentException ex) {

            model.addAttribute(
                    "error",
                    ex.getMessage());

            return "error";
        }
    }

    @GetMapping("/scan")
    public String scanPage() {
        return "visitor-scan";
    }

    @GetMapping("/{visitorCode}/verify")
    public String verifyVisitor(
            @PathVariable String visitorCode,
            Model model) {

        try {

            VisitorService.VisitorProfile profile = visitorService.loadProfile(visitorCode);

            VisitorVerificationService.VerificationResult result = verificationService.verify(profile);

            Visitor visitor = profile.visitor();

            List<Farm> farms = farmService.findAll();

            List<FarmVisit> recentFarmVisits = visitorService.getRecentFarmVisits(
                    visitor.getId(),
                    5);

            model.addAttribute(
                    "profile",
                    profile);

            model.addAttribute(
                    "verification",
                    result);

            model.addAttribute(
                    "recentFarmVisits",
                    recentFarmVisits);

            model.addAttribute("farms", farms);

            return "visitor-verification";

        } catch (IllegalArgumentException ex) {

            model.addAttribute(
                    "error",
                    ex.getMessage());

            return "error";
        }
    }

    @GetMapping("/{visitorCode}/check-in")
    public String checkInForm(
            @PathVariable String visitorCode,
            Model model) {

        try {
            VisitorService.VisitorProfile profile = visitorService.loadProfile(visitorCode);

            VisitorVerificationService.VerificationResult verification = verificationService.verify(profile);

            if (!"ALLOWED".equalsIgnoreCase(
                    verification.decision())) {

                model.addAttribute("profile", profile);
                model.addAttribute("verification", verification);

                return "visitor-verification";
            }

            List<Farm> farms = farmService.findAll();

            model.addAttribute("profile", profile);
            model.addAttribute("farms", farms);

            return "visitor-check-in";

        } catch (IllegalArgumentException ex) {

            model.addAttribute("error", ex.getMessage());

            return "error";
        }
    }

    @PostMapping("/{visitorCode}/check-in")
    public String checkIn(
            @PathVariable String visitorCode,
            @RequestParam Long farmId,
            @RequestParam String purpose,
            @RequestParam(required = false) String vehicleNumber,
            Model model) {

        try {

            Farm farm = farmService.findById(farmId);

            FarmVisit visit = checkInService.checkIn(
                    visitorCode,
                    farm,
                    purpose,
                    vehicleNumber);

            return "redirect:/visitors/check-in-success/"
                    + visit.getId();

        } catch (IllegalArgumentException ex) {

            VisitorService.VisitorProfile profile = visitorService.loadProfile(visitorCode);

            List<Farm> farms = farmService.findAll();

            model.addAttribute("profile", profile);
            model.addAttribute("farms", farms);
            model.addAttribute("error", ex.getMessage());

            return "visitor-check-in";
        }
    }

    @GetMapping("/check-in-success/{visitId}")
    public String checkInSuccess(
            @PathVariable Long visitId,
            Model model) {

        try {

            FarmVisit visit = farmVisitRepository
                    .findById(visitId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Farm visit not found."));

            model.addAttribute("visit", visit);

            return "visitor-check-in-success";

        } catch (IllegalArgumentException ex) {

            model.addAttribute("error", ex.getMessage());

            return "error";
        }
    }

    @PostMapping("/{visitorCode}/biosecurity/verify")
    public String verifyBiosecurity(
            @PathVariable String visitorCode,
            @RequestParam(required = false) String remarks) {

        Visitor visitor = visitorService
                .findByVisitorCode(visitorCode)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Visitor not found."));

        visitorService.verifyBiosecurity(
                visitor.getId(),
                remarks);

        return "redirect:/visitors/"
                + visitorCode
                + "/verify";
    }

    @PostMapping("/check-out/{visitId}")
    public String checkOut(
            @PathVariable Long visitId,
            Model model) {

        try {

            FarmVisit visit = checkInService.checkOut(visitId);

            return "redirect:/visitors";

        } catch (IllegalArgumentException ex) {

            model.addAttribute("error", ex.getMessage());

            return "error";
        }
    }

    /*
     * Display a visitor profile.
     */
    @GetMapping("/{visitorCode}")
    public String profile(
            @PathVariable String visitorCode,
            Model model) {

        try {

            VisitorService.VisitorProfile profile = visitorService.loadProfile(visitorCode);

            Visitor visitor = profile.visitor();

            List<FarmVisit> visitHistory = visitorService.getVisitHistory(
                    visitor.getId());

            List<FarmVisit> recentFarmVisits = visitorService.getRecentFarmVisits(
                    visitor.getId(),
                    5);

            model.addAttribute(
                    "recentFarmVisits",
                    recentFarmVisits);

            model.addAttribute("profile", profile);

            model.addAttribute(
                    "visitHistory",
                    visitHistory);

            String qrCode = qrCodeService.generateBase64(
                    visitor.getVisitorCode());

            model.addAttribute(
                    "qrCode",
                    qrCode);

            return "visitor-profile";

        } catch (IllegalArgumentException ex) {

            model.addAttribute(
                    "error",
                    ex.getMessage());

            return "error";
        }
    }

    @PostMapping("/licence/{licenceId}/verify")
    public String verifyLicence(
            @PathVariable Long licenceId,
            @RequestParam(required = false) String remarks) {

        visitorService.verifyLicence(licenceId, remarks);

        return "redirect:/visitors";
    }

    @PostMapping("/licence/{licenceId}/reject")
    public String rejectLicence(
            @PathVariable Long licenceId,
            @RequestParam(required = false) String remarks) {

        visitorService.rejectLicence(licenceId, remarks);

        return "redirect:/visitors";
    }

    @GetMapping("/licence/{licenceId}/review")
    public String reviewLicence(
            @PathVariable Long licenceId,
            Model model) {

        VisitorLicence licence = visitorService.findLicenceById(licenceId);

        model.addAttribute("licence", licence);

        return "visitor-licence-review";
    }
}