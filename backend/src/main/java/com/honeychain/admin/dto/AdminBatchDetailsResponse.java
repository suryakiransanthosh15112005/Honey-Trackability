package com.honeychain.admin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.honeychain.batch.entity.BatchStatus;
import com.honeychain.lab.entity.LabTestResult;
import com.honeychain.verification.entity.VerificationRiskLevel;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AdminBatchDetailsResponse {

    // Batch Info
    private Long id;
    private String batchId;
    private Double quantityKg;
    private LocalDate harvestDate;
    private BatchStatus status;
    private String photoUrl;

    // Beekeeper Info
    private Long beekeeperId;
    private String beekeeperName;
    private String beekeeperVillage;
    private String beekeeperKvicId;

    // Hive Info
    private Long hiveId;
    private String hiveCode;
    private String clusterName;

    // Lab Info
    private boolean labTested;
    private Integer purityScore;
    private LabTestResult labResult;
    private String labName;
    private Double moisturePercentage;
    private Double sucrosePercentage;
    private Integer pollenCount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime testDate;

    // Blockchain Info
    private boolean blockchainRecorded;
    private String dataHash;
    private String transactionHash;
    private Long blockNumber;

    // QR Info
    private boolean qrGenerated;
    private String qrCodeUrl;
    private String publicVerificationUrl;

    // Verification / Anti-Counterfeit Activity
    private long totalScans;
    private VerificationRiskLevel riskLevel;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastScannedAt;

    // Marketplace Status
    private boolean productListed;
    private Long productId;
    private Double pricePerKg;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public AdminBatchDetailsResponse() {
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

    public Double getQuantityKg() {
        return quantityKg;
    }

    public void setQuantityKg(Double quantityKg) {
        this.quantityKg = quantityKg;
    }

    public LocalDate getHarvestDate() {
        return harvestDate;
    }

    public void setHarvestDate(LocalDate harvestDate) {
        this.harvestDate = harvestDate;
    }

    public BatchStatus getStatus() {
        return status;
    }

    public void setStatus(BatchStatus status) {
        this.status = status;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Long getBeekeeperId() {
        return beekeeperId;
    }

    public void setBeekeeperId(Long beekeeperId) {
        this.beekeeperId = beekeeperId;
    }

    public String getBeekeeperName() {
        return beekeeperName;
    }

    public void setBeekeeperName(String beekeeperName) {
        this.beekeeperName = beekeeperName;
    }

    public String getBeekeeperVillage() {
        return beekeeperVillage;
    }

    public void setBeekeeperVillage(String beekeeperVillage) {
        this.beekeeperVillage = beekeeperVillage;
    }

    public String getBeekeeperKvicId() {
        return beekeeperKvicId;
    }

    public void setBeekeeperKvicId(String beekeeperKvicId) {
        this.beekeeperKvicId = beekeeperKvicId;
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

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public boolean isLabTested() {
        return labTested;
    }

    public void setLabTested(boolean labTested) {
        this.labTested = labTested;
    }

    public Integer getPurityScore() {
        return purityScore;
    }

    public void setPurityScore(Integer purityScore) {
        this.purityScore = purityScore;
    }

    public LabTestResult getLabResult() {
        return labResult;
    }

    public void setLabResult(LabTestResult labResult) {
        this.labResult = labResult;
    }

    public String getLabName() {
        return labName;
    }

    public void setLabName(String labName) {
        this.labName = labName;
    }

    public Double getMoisturePercentage() {
        return moisturePercentage;
    }

    public void setMoisturePercentage(Double moisturePercentage) {
        this.moisturePercentage = moisturePercentage;
    }

    public Double getSucrosePercentage() {
        return sucrosePercentage;
    }

    public void setSucrosePercentage(Double sucrosePercentage) {
        this.sucrosePercentage = sucrosePercentage;
    }

    public Integer getPollenCount() {
        return pollenCount;
    }

    public void setPollenCount(Integer pollenCount) {
        this.pollenCount = pollenCount;
    }

    public LocalDateTime getTestDate() {
        return testDate;
    }

    public void setTestDate(LocalDateTime testDate) {
        this.testDate = testDate;
    }

    public boolean isBlockchainRecorded() {
        return blockchainRecorded;
    }

    public void setBlockchainRecorded(boolean blockchainRecorded) {
        this.blockchainRecorded = blockchainRecorded;
    }

    public String getDataHash() {
        return dataHash;
    }

    public void setDataHash(String dataHash) {
        this.dataHash = dataHash;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public Long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(Long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public boolean isQrGenerated() {
        return qrGenerated;
    }

    public void setQrGenerated(boolean qrGenerated) {
        this.qrGenerated = qrGenerated;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public String getPublicVerificationUrl() {
        return publicVerificationUrl;
    }

    public void setPublicVerificationUrl(String publicVerificationUrl) {
        this.publicVerificationUrl = publicVerificationUrl;
    }

    public long getTotalScans() {
        return totalScans;
    }

    public void setTotalScans(long totalScans) {
        this.totalScans = totalScans;
    }

    public VerificationRiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(VerificationRiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public LocalDateTime getLastScannedAt() {
        return lastScannedAt;
    }

    public void setLastScannedAt(LocalDateTime lastScannedAt) {
        this.lastScannedAt = lastScannedAt;
    }

    public boolean isProductListed() {
        return productListed;
    }

    public void setProductListed(boolean productListed) {
        this.productListed = productListed;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Double getPricePerKg() {
        return pricePerKg;
    }

    public void setPricePerKg(Double pricePerKg) {
        this.pricePerKg = pricePerKg;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
