package com.liveshield.repository;

import com.liveshield.entity.VisitorLicence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VisitorLicenceRepository
        extends JpaRepository<VisitorLicence, Long> {

    List<VisitorLicence> findByVisitorId(Long visitorId);

    Optional<VisitorLicence> findTopByVisitorIdOrderByExpiryDateDesc(
            Long visitorId
    );

    Optional<VisitorLicence> findByLicenceNumber(
            String licenceNumber
    );
}