package com.honeychain.verification.repository;

import com.honeychain.verification.entity.VerificationHistory;
import com.honeychain.verification.entity.VerificationRiskLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VerificationHistoryRepository extends JpaRepository<VerificationHistory, Long> {

    long countByBatchId(String batchId);

    long countByBatchIdAndScannedAtAfter(String batchId, LocalDateTime after);

    Optional<VerificationHistory> findTop1ByBatchIdOrderByScannedAtDesc(String batchId);

    Optional<VerificationHistory> findTop1ByBatchIdAndVerificationFingerprintOrderByScannedAtDesc(
            String batchId, String verificationFingerprint);

    List<VerificationHistory> findTop10ByBatchIdOrderByScannedAtDesc(String batchId);

    long countByRiskLevel(VerificationRiskLevel riskLevel);
}
