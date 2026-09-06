package com.honeychain.verification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.honeychain.verification.entity.VerificationRiskLevel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PublicVerificationResponse {

    private boolean verified;
    private String verificationStatus;
    private String message;
    private String batchId;

    // Beekeeper Public Info (No private phone, password, or exact IDs)
    private BeekeeperPublicInfo beekeeper;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate harvestDate;

    private BigDecimal quantityKg;
    private String batchPhotoUrl;
    private String hiveCode;
    private String clusterName;

    // Purity Details
    private PurityPublicInfo purity;

    // Blockchain Verification
    private BlockchainVerificationSummary blockchain;

    // Milestone Timeline
    private List<VerificationTimelineItem> timeline;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime qrGeneratedAt;

    // Verification History & Risk Summary
    private VerificationHistorySummary verificationHistory;

    public PublicVerificationResponse() {
    }

    public static class BeekeeperPublicInfo {
        private String name;
        private String village;
        private String photoUrl;

        public BeekeeperPublicInfo() {
        }

        public BeekeeperPublicInfo(String name, String village, String photoUrl) {
            this.name = name;
            this.village = village;
            this.photoUrl = photoUrl;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVillage() {
            return village;
        }

        public void setVillage(String village) {
            this.village = village;
        }

        public String getPhotoUrl() {
            return photoUrl;
        }

        public void setPhotoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
        }
    }

    public static class PurityPublicInfo {
        private Integer score;
        private String result;
        private String certificateUrl;
        private String remarks;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime testedAt;

        public PurityPublicInfo() {
        }

        public PurityPublicInfo(Integer score, String result, String certificateUrl, String remarks,
                LocalDateTime testedAt) {
            this.score = score;
            this.result = result;
            this.certificateUrl = certificateUrl;
            this.remarks = remarks;
            this.testedAt = testedAt;
        }

        public Integer getScore() {
            return score;
        }

        public void setScore(Integer score) {
            this.score = score;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public String getCertificateUrl() {
            return certificateUrl;
        }

        public void setCertificateUrl(String certificateUrl) {
            this.certificateUrl = certificateUrl;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }

        public LocalDateTime getTestedAt() {
            return testedAt;
        }

        public void setTestedAt(LocalDateTime testedAt) {
            this.testedAt = testedAt;
        }
    }

    public static class VerificationHistorySummary {
        private long totalVerifications;
        private VerificationRiskLevel riskLevel;
        private String riskMessage;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime lastVerifiedAt;

        public VerificationHistorySummary() {
        }

        public VerificationHistorySummary(long totalVerifications, VerificationRiskLevel riskLevel, String riskMessage,
                LocalDateTime lastVerifiedAt) {
            this.totalVerifications = totalVerifications;
            this.riskLevel = riskLevel;
            this.riskMessage = riskMessage;
            this.lastVerifiedAt = lastVerifiedAt;
        }

        public long getTotalVerifications() {
            return totalVerifications;
        }

        public void setTotalVerifications(long totalVerifications) {
            this.totalVerifications = totalVerifications;
        }

        public VerificationRiskLevel getRiskLevel() {
            return riskLevel;
        }

        public void setRiskLevel(VerificationRiskLevel riskLevel) {
            this.riskLevel = riskLevel;
        }

        public String getRiskMessage() {
            return riskMessage;
        }

        public void setRiskMessage(String riskMessage) {
            this.riskMessage = riskMessage;
        }

        public LocalDateTime getLastVerifiedAt() {
            return lastVerifiedAt;
        }

        public void setLastVerifiedAt(LocalDateTime lastVerifiedAt) {
            this.lastVerifiedAt = lastVerifiedAt;
        }
    }

    // Getters and Setters
    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public BeekeeperPublicInfo getBeekeeper() {
        return beekeeper;
    }

    public void setBeekeeper(BeekeeperPublicInfo beekeeper) {
        this.beekeeper = beekeeper;
    }

    public LocalDate getHarvestDate() {
        return harvestDate;
    }

    public void setHarvestDate(LocalDate harvestDate) {
        this.harvestDate = harvestDate;
    }

    public BigDecimal getQuantityKg() {
        return quantityKg;
    }

    public void setQuantityKg(BigDecimal quantityKg) {
        this.quantityKg = quantityKg;
    }

    public String getBatchPhotoUrl() {
        return batchPhotoUrl;
    }

    public void setBatchPhotoUrl(String batchPhotoUrl) {
        this.batchPhotoUrl = batchPhotoUrl;
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

    public PurityPublicInfo getPurity() {
        return purity;
    }

    public void setPurity(PurityPublicInfo purity) {
        this.purity = purity;
    }

    public BlockchainVerificationSummary getBlockchain() {
        return blockchain;
    }

    public void setBlockchain(BlockchainVerificationSummary blockchain) {
        this.blockchain = blockchain;
    }

    public List<VerificationTimelineItem> getTimeline() {
        return timeline;
    }

    public void setTimeline(List<VerificationTimelineItem> timeline) {
        this.timeline = timeline;
    }

    public LocalDateTime getQrGeneratedAt() {
        return qrGeneratedAt;
    }

    public void setQrGeneratedAt(LocalDateTime qrGeneratedAt) {
        this.qrGeneratedAt = qrGeneratedAt;
    }

    public VerificationHistorySummary getVerificationHistory() {
        return verificationHistory;
    }

    public void setVerificationHistory(VerificationHistorySummary verificationHistory) {
        this.verificationHistory = verificationHistory;
    }
}
