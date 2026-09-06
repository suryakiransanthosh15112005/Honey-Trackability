package com.honeychain.ai.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class YieldPredictionResponse {

    private Long hiveId;
    private String hiveCode;
    private LocalDate predictedHarvestDate;
    private long daysUntilHarvest;
    private BigDecimal minimumKg;
    private BigDecimal maximumKg;
    private Integer confidence;
    private String healthStatus;
    private String message;
    private String explanation;
    private LocalDateTime generatedAt;
    private YieldPredictionExplanation explanationDetails;

    public YieldPredictionResponse() {
    }

    public YieldPredictionResponse(Long hiveId, String hiveCode, LocalDate predictedHarvestDate, long daysUntilHarvest,
                                   BigDecimal minimumKg, BigDecimal maximumKg, Integer confidence, String healthStatus,
                                   String message, String explanation, LocalDateTime generatedAt,
                                   YieldPredictionExplanation explanationDetails) {
        this.hiveId = hiveId;
        this.hiveCode = hiveCode;
        this.predictedHarvestDate = predictedHarvestDate;
        this.daysUntilHarvest = daysUntilHarvest;
        this.minimumKg = minimumKg;
        this.maximumKg = maximumKg;
        this.confidence = confidence;
        this.healthStatus = healthStatus;
        this.message = message;
        this.explanation = explanation;
        this.generatedAt = generatedAt;
        this.explanationDetails = explanationDetails;
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

    public LocalDate getPredictedHarvestDate() {
        return predictedHarvestDate;
    }

    public void setPredictedHarvestDate(LocalDate predictedHarvestDate) {
        this.predictedHarvestDate = predictedHarvestDate;
    }

    public long getDaysUntilHarvest() {
        return daysUntilHarvest;
    }

    public void setDaysUntilHarvest(long daysUntilHarvest) {
        this.daysUntilHarvest = daysUntilHarvest;
    }

    public BigDecimal getMinimumKg() {
        return minimumKg;
    }

    public void setMinimumKg(BigDecimal minimumKg) {
        this.minimumKg = minimumKg;
    }

    public BigDecimal getMaximumKg() {
        return maximumKg;
    }

    public void setMaximumKg(BigDecimal maximumKg) {
        this.maximumKg = maximumKg;
    }

    public Integer getConfidence() {
        return confidence;
    }

    public void setConfidence(Integer confidence) {
        this.confidence = confidence;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public YieldPredictionExplanation getExplanationDetails() {
        return explanationDetails;
    }

    public void setExplanationDetails(YieldPredictionExplanation explanationDetails) {
        this.explanationDetails = explanationDetails;
    }
}
