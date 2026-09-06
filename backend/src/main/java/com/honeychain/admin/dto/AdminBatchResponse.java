package com.honeychain.admin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.honeychain.batch.entity.BatchStatus;
import com.honeychain.verification.entity.VerificationRiskLevel;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AdminBatchResponse {

    private Long id;
    private String batchId;
    private Long beekeeperProfileId;
    private String beekeeperName;
    private String village;
    private Long hiveId;
    private String hiveCode;
    private LocalDate harvestDate;
    private Double quantityKg;
    private BatchStatus status;
    private Integer purityScore;
    private String blockchainTxHash;
    private boolean qrGenerated;
    private long verificationCount;
    private VerificationRiskLevel riskLevel;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public AdminBatchResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public Long getBeekeeperProfileId() {
        return beekeeperProfileId;
    }

    public void setBeekeeperProfileId(Long beekeeperProfileId) {
        this.beekeeperProfileId = beekeeperProfileId;
    }

    public String getBeekeeperName() {
        return beekeeperName;
    }

    public void setBeekeeperName(String beekeeperName) {
        this.beekeeperName = beekeeperName;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public Long getHiveId() {
        return hiveId;
    }

    public void setHiveId(Long hiveId) {
        this.hiveId = hiveId;
    }

    public String getHiveCode() {
        return hiveCode;
    }

    public void setHiveCode(String hiveCode) {
        this.hiveCode = hiveCode;
    }

    public LocalDate getHarvestDate() {
        return harvestDate;
    }

    public void setHarvestDate(LocalDate harvestDate) {
        this.harvestDate = harvestDate;
    }

    public Double getQuantityKg() {
        return quantityKg;
    }

    public void setQuantityKg(Double quantityKg) {
        this.quantityKg = quantityKg;
    }

    public BatchStatus getStatus() {
        return status;
    }

    public void setStatus(BatchStatus status) {
        this.status = status;
    }

    public Integer getPurityScore() {
        return purityScore;
    }

    public void setPurityScore(Integer purityScore) {
        this.purityScore = purityScore;
    }

    public String getBlockchainTxHash() {
        return blockchainTxHash;
    }

    public void setBlockchainTxHash(String blockchainTxHash) {
        this.blockchainTxHash = blockchainTxHash;
    }

    public boolean isQrGenerated() {
        return qrGenerated;
    }

    public void setQrGenerated(boolean qrGenerated) {
        this.qrGenerated = qrGenerated;
    }

    public long getVerificationCount() {
        return verificationCount;
    }

    public void setVerificationCount(long verificationCount) {
        this.verificationCount = verificationCount;
    }

    public VerificationRiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(VerificationRiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
