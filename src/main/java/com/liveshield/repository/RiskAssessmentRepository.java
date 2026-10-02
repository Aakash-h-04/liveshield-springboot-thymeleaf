package com.liveshield.repository;

import com.liveshield.entity.RiskAssessment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RiskAssessmentRepository
        extends JpaRepository<RiskAssessment, Long> {

    @EntityGraph(attributePaths = {"farm"})
    Optional<RiskAssessment> findTopByFarmIdOrderByAssessedAtDesc(
            Long farmId
    );
}