package com.liveshield.service;

import com.liveshield.entity.DailyLog;
import com.liveshield.entity.Farm;
import com.liveshield.repository.DailyLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Service
public class DailyLogService {

    private final DailyLogRepository dailyLogRepository;
    private final FarmService farmService;

    public DailyLogService(
            DailyLogRepository dailyLogRepository,
            FarmService farmService) {

        this.dailyLogRepository = dailyLogRepository;
        this.farmService = farmService;
    }

    public DailyLog getTodayLog(Long farmId) {

        LocalDate today = LocalDate.now();

        return dailyLogRepository
                .findByFarmIdAndLogDate(farmId, today)
                .orElseGet(() -> {

                    DailyLog log = new DailyLog();

                    Farm farm = farmService.findById(farmId);

                    log.setFarm(farm);
                    log.setLogDate(today);

                    return log;
                });
    }

    @Transactional
    public DailyLog save(DailyLog log) {

        log.calculateCompliance();

        DailyLog saved = dailyLogRepository.save(log);

        updateFarmStatus(saved);

        return saved;
    }

    private void updateFarmStatus(DailyLog log) {

    Farm farm = log.getFarm();

    int compliance = log.getCompliancePercent();

    farm.setCompliancePercent(compliance);

    /*
     * Daily compliance should NOT overwrite the
     * Risk Assessment result.
     *
     * Risk level is owned by the Risk Assessment module.
     */
    farmService.save(farm);
}
}

