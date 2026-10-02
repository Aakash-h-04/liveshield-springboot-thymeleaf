package com.liveshield.repository;

import com.liveshield.entity.LivestockBatch;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LivestockBatchRepository
        extends JpaRepository<LivestockBatch, Long> {

    @EntityGraph(attributePaths = {"farm"})
    List<LivestockBatch> findByFarmIdOrderByArrivalDateDesc(Long farmId);

    @EntityGraph(attributePaths = {"farm"})
    Optional<LivestockBatch> findByIdAndFarmId(
            Long id,
            Long farmId
    );
}