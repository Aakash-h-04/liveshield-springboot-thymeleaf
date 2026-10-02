package com.liveshield.repository;

import com.liveshield.entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VisitorRepository
        extends JpaRepository<Visitor, Long> {

    Optional<Visitor> findByVisitorCode(String visitorCode);

    Optional<Visitor> findByMobile(String mobile);

    boolean existsByVisitorCode(String visitorCode);

    // All registered visitors whose account is active
    List<Visitor> findByAccountStatusOrderByFullNameAsc(String accountStatus);
}