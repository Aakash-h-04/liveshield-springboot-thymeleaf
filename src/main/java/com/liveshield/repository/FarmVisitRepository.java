package com.liveshield.repository;

import com.liveshield.entity.FarmVisit;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

public interface FarmVisitRepository
        extends JpaRepository<FarmVisit, Long> {

    @EntityGraph(attributePaths = { "visitor", "farm" })
    List<FarmVisit> findByVisitorIdOrderByCheckInTimeDesc(
            Long visitorId);

    @EntityGraph(attributePaths = { "visitor", "farm" })
    List<FarmVisit> findByFarmIdOrderByCheckInTimeDesc(
            Long farmId);

    @EntityGraph(attributePaths = { "visitor", "farm" })
    Optional<FarmVisit> findTopByVisitorIdOrderByCheckInTimeDesc(
            Long visitorId);

    @EntityGraph(attributePaths = { "visitor", "farm" })
    List<FarmVisit> findByFarmIdAndCheckOutTimeIsNullOrderByCheckInTimeDesc(
            Long farmId);

    @EntityGraph(attributePaths = { "visitor", "farm" })
    Optional<FarmVisit> findByPassCode(String passCode);

    List<FarmVisit> findByEntryDecisionOrderByCheckInTimeDesc(
            String entryDecision);

    @EntityGraph(attributePaths = { "visitor", "farm" })
    Optional<FarmVisit> findById(Long id);

    @EntityGraph(attributePaths = { "visitor", "farm" })
    List<FarmVisit> findByCheckOutTimeIsNullOrderByCheckInTimeDesc();

    long countByCheckOutTimeIsNull();

    long countByCheckInTimeGreaterThanEqual(
            LocalDateTime start);

    long countByCheckOutTimeGreaterThanEqual(
            LocalDateTime start);

    
}