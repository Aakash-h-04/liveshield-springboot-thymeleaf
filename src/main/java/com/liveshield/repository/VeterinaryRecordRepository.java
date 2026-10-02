package com.liveshield.repository;

import com.liveshield.entity.VeterinaryRecord;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VeterinaryRecordRepository
        extends JpaRepository<VeterinaryRecord, Long> {

    @EntityGraph(attributePaths = {"batch", "batch.farm"})
    List<VeterinaryRecord> findByBatchIdOrderByRecordDateDesc(
            Long batchId
    );

    @Query("""
        SELECT r
        FROM VeterinaryRecord r
        JOIN FETCH r.batch b
        JOIN FETCH b.farm f
        WHERE f.id = :farmId
        ORDER BY r.recordDate DESC
    """)
    List<VeterinaryRecord> findByFarmIdOrderByRecordDateDesc(
            @Param("farmId") Long farmId
    );
}