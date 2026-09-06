package com.honeychain.lab.repository;

import com.honeychain.lab.entity.LabTest;
import com.honeychain.lab.entity.LabTestResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LabTestRepository extends JpaRepository<LabTest, Long> {

    Optional<LabTest> findByBatchId(String batchId);

    boolean existsByBatchId(String batchId);

    List<LabTest> findAllByResult(LabTestResult result);

    long countByResult(LabTestResult result);

    @Query("SELECT AVG(l.purityScore) FROM LabTest l WHERE l.purityScore IS NOT NULL")
    Double findAveragePurityScore();

    @Query("SELECT l FROM LabTest l WHERE (:result IS NULL OR l.result = :result)")
    Page<LabTest> findAdminLabTests(@Param("result") LabTestResult result, Pageable pageable);
}
