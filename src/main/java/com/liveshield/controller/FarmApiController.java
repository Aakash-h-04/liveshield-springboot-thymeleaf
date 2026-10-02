package com.liveshield.controller;

import com.liveshield.entity.Farm;
import com.liveshield.service.FarmService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/farms")
public class FarmApiController {
    private final FarmService service;
    public FarmApiController(FarmService service) { this.service = service; }

    @GetMapping public List<Farm> all() { return service.findAll(); }
    @GetMapping("/{id}") public Farm one(@PathVariable Long id) { return service.findById(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public Farm create(@Valid @RequestBody Farm farm) { return service.save(farm); }
    @PutMapping("/{id}") public Farm update(@PathVariable Long id, @Valid @RequestBody Farm input) {
        Farm farm = service.findById(id);
        farm.setName(input.getName()); farm.setOwnerName(input.getOwnerName()); farm.setFarmType(input.getFarmType());
        farm.setLocation(input.getLocation()); farm.setLivestockCount(input.getLivestockCount());
        farm.setCompliancePercent(input.getCompliancePercent()); farm.setRiskLevel(input.getRiskLevel());
        return service.save(farm);
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id) { service.delete(id); }
}
