package com.liveshield.service;

import com.liveshield.entity.Farm;
import com.liveshield.exception.FarmNotFoundException;
import com.liveshield.repository.FarmRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FarmService {

    private final FarmRepository repository;

    public FarmService(FarmRepository repository) {
        this.repository = repository;
    }

    public List<Farm> findAll() {
        return repository.findAll();
    }

    public Farm findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new FarmNotFoundException(id));
    }

    public Farm save(Farm farm) {
        return repository.save(farm);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}