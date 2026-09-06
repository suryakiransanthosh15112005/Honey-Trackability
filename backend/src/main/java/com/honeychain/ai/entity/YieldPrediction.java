package com.honeychain.ai.entity;

import com.honeychain.common.entity.BaseEntity;
import com.honeychain.hive.entity.Hive;
import com.honeychain.iot.entity.HiveHealthStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "yield_predictions", indexes = {
        @Index(name = "idx_predictions_hive_id", columnList = "hive_id"),
        @Index(name = "idx_predictions_generated_at", columnList = "generated_at"),
        @Index(name = "idx_predictions_hive_generated", columnList = "hive_id, generated_at")
})
public class YieldPrediction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hive_id", nullable = false)
    private Hive hive;

    @Column(name = "predicted_harvest_date", nullable = false)
    private LocalDate predictedHarvestDate;

    @Column(name = "minimum_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal minimumKg;

    @Column(name = "maximum_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal maximumKg;

    @Column(name = "confidence", nullable = false)
    private Integer confidence;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    @Column(name = "explanation", nullable = false, length = 1000)
    private String explanation;

    @Enumerated(EnumType.STRING)
    @Column(name = "health_status", nullable = false, length = 20)
    private HiveHealthStatus healthStatus = HiveHealthStatus.HEALTHY;

    public YieldPrediction() {
    }

    public YieldPrediction(Hive hive, LocalDate predictedHarvestDate, BigDecimal minimumKg, BigDecimal maximumKg,
            Integer confidence, LocalDateTime generatedAt, String explanation, HiveHealthStatus healthStatus) {
        this.hive = hive;
        this.predictedHarvestDate = predictedHarvestDate;
        this.minimumKg = minimumKg;
        this.maximumKg = maximumKg;
        this.confidence = confidence;
        this.generatedAt = generatedAt;
        this.explanation = explanation;
        this.healthStatus = healthStatus;
    }

    public Hive getHive() {
        return hive;
    }

    public void setHive(Hive hive) {
        this.hive = hive;
    }

    public LocalDate getPredictedHarvestDate() {
        return predictedHarvestDate;
    }

    public void setPredictedHarvestDate(LocalDate predictedHarvestDate) {
        this.predictedHarvestDate = predictedHarvestDate;
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

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public HiveHealthStatus getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(HiveHealthStatus healthStatus) {
        this.healthStatus = healthStatus;
    }
}
