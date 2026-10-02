package com.liveshield.repository;

import com.liveshield.entity.DailyLog;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {

    @EntityGraph(attributePaths = {"farm"})
    Optional<DailyLog> findByFarmIdAndLogDate(
            Long farmId,
            LocalDate logDate
    );
}