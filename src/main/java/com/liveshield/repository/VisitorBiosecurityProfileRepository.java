package com.liveshield.repository;

import com.liveshield.entity.VisitorBiosecurityProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VisitorBiosecurityProfileRepository
        extends JpaRepository<VisitorBiosecurityProfile, Long> {

    Optional<VisitorBiosecurityProfile> findByVisitorId(
            Long visitorId
    );
}