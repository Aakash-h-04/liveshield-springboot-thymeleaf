package com.liveshield.service;

import com.liveshield.entity.LivestockBatch;
import com.liveshield.entity.VeterinaryRecord;
import com.liveshield.repository.LivestockBatchRepository;
import com.liveshield.repository.VeterinaryRecordRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VeterinaryRecordService {

    private final VeterinaryRecordRepository repository;
    private final LivestockBatchRepository batchRepository;
    @SuppressWarnings("unused")
    private final AlertService alertService;

    public VeterinaryRecordService(
            VeterinaryRecordRepository repository,
            LivestockBatchRepository batchRepository,
            AlertService alertService) {

        this.repository = repository;
        this.batchRepository = batchRepository;
        this.alertService = alertService;
    }

    public List<VeterinaryRecord> findByFarm(Long farmId) {

        return repository
                .findByFarmIdOrderByRecordDateDesc(farmId);

    }

    public List<VeterinaryRecord> findByBatch(Long batchId) {

        return repository
                .findByBatchIdOrderByRecordDateDesc(batchId);
    }

    @Transactional
    public VeterinaryRecord create(
            Long farmId,
            Long batchId,
            VeterinaryRecord record) {

        LivestockBatch batch = batchRepository
                .findByIdAndFarmId(batchId, farmId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Livestock batch not found: "
                                + batchId));

        record.setBatch(batch);

        VeterinaryRecord saved = repository.save(record);

        /*
         * A disease record represents a biosecurity event.
         * Create an alert automatically.
         */
        if ("DISEASE".equals(saved.getRecordType())) {

            batch.setHealthStatus("SICK");

            batchRepository.save(batch);

            alertService.createVeterinaryAlert(saved);
        }

        return saved;
    }

    @Transactional
    public void delete(
            Long farmId,
            Long batchId,
            Long recordId) {

        VeterinaryRecord record = repository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Veterinary record not found: "
                                + recordId));

        if (!record.getBatch().getId().equals(batchId)
                || !record.getBatch().getFarm().getId().equals(farmId)) {

            throw new IllegalArgumentException(
                    "Veterinary record does not belong to this farm");
        }

        repository.delete(record);
    }
}