package com.liveshield.service;

import com.liveshield.entity.Farm;
import com.liveshield.entity.LivestockBatch;
import com.liveshield.repository.LivestockBatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LivestockBatchService {

    private final LivestockBatchRepository repository;
    private final FarmService farmService;

    public LivestockBatchService(
            LivestockBatchRepository repository,
            FarmService farmService) {

        this.repository = repository;
        this.farmService = farmService;
    }

    public List<LivestockBatch> findByFarm(Long farmId) {

        return repository.findByFarmIdOrderByArrivalDateDesc(
                farmId
        );
    }

    public LivestockBatch get(Long farmId, Long id) {

        return repository.findByIdAndFarmId(id, farmId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Livestock batch not found: " + id
                        ));
    }

    @Transactional
    public LivestockBatch create(
            Long farmId,
            LivestockBatch batch) {

        Farm farm = farmService.findById(farmId);

        batch.setFarm(farm);

        return repository.save(batch);
    }

    @Transactional
    public LivestockBatch update(
            Long farmId,
            Long id,
            LivestockBatch updated) {

        LivestockBatch existing = get(farmId, id);

        existing.setBatchCode(updated.getBatchCode());
        existing.setSpecies(updated.getSpecies());
        existing.setBreed(updated.getBreed());
        existing.setAnimalCount(updated.getAnimalCount());
        existing.setArrivalDate(updated.getArrivalDate());
        existing.setSource(updated.getSource());
        existing.setHealthStatus(updated.getHealthStatus());

        return repository.save(existing);
    }

    @Transactional
    public void delete(Long farmId, Long id) {

        LivestockBatch batch = get(farmId, id);

        repository.delete(batch);
    }
}