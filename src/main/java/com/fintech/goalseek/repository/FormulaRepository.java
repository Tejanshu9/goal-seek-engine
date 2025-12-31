package com.fintech.goalseek.repository;

import com.fintech.goalseek.entity.Formula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Formula entity operations.
 */
@Repository
public interface FormulaRepository extends JpaRepository<Formula, Long> {

    Optional<Formula> findByName(String name);

    boolean existsByName(String name);

    void deleteByName(String name);
}
