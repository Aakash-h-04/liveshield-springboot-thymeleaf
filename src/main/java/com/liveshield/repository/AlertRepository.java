package com.liveshield.repository;

import com.liveshield.entity.Alert;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlertRepository
        extends JpaRepository<Alert, Long> {

    @EntityGraph(attributePaths = { "farm" })
    List<Alert> findByResolvedFalseOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = { "farm" })
    List<Alert> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = { "farm" })
    Optional<Alert> findTopByFarmIdAndSourceOrderByCreatedAtDesc(
            Long farmId,
            String source);




}