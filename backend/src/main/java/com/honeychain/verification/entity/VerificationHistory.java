package com.honeychain.verification.entity;

import com.honeychain.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "verification_histories",
        indexes = {
                @Index(name = "idx_verif_hist_batch_id", columnList = "batch_id"),
                @Index(name = "idx_verif_hist_scanned_at", columnList = "scanned_at"),
                @Index(name = "idx_verif_hist_risk_level", columnList = "risk_level"),
                @Index(name = "idx_verif_hist_batch_scanned", columnList = "batch_id, scanned_at")
        }
)
public class VerificationHistory extends BaseEntity {

    @Column(name = "batch_id", nullable = false, length = 30)
    private String batchId;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "verification_fingerprint", nullable = false, length = 64)
    private String verificationFingerprint;

    @Enumerated(EnumType.STRING)
    @Column(name = "result", nullable = false, length = 30)
    private VerificationResult result;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false, length = 30)
    private VerificationRiskLevel riskLevel;

    @Column(name = "scanned_at", nullable = false)
    private LocalDateTime scannedAt;

    public VerificationHistory() {
    }

    public VerificationHistory(String batchId, Long customerId, String verificationFingerprint,
                               VerificationResult result, VerificationRiskLevel riskLevel, LocalDateTime scannedAt) {
        this.batchId = batchId;
        this.customerId = customerId;
        this.verificationFingerprint = verificationFingerprint;
        this.result = result;
        this.riskLevel = riskLevel;
        this.scannedAt = scannedAt != null ? scannedAt : LocalDateTime.now();
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getVerificationFingerprint() {
        return verificationFingerprint;
    }

    public void setVerificationFingerprint(String verificationFingerprint) {
        this.verificationFingerprint = verificationFingerprint;
    }

    public VerificationResult getResult() {
        return result;
    }

    public void setResult(VerificationResult result) {
        this.result = result;
    }

    public VerificationRiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(VerificationRiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public LocalDateTime getScannedAt() {
        return scannedAt;
    }

    public void setScannedAt(LocalDateTime scannedAt) {
        this.scannedAt = scannedAt;
    }
}
