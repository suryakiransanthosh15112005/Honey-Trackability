package com.honeychain.ai.dto;

import java.math.BigDecimal;

public class YieldPredictionExplanation {

    private int historicalDataPoints;
    private BigDecimal averageHistoricalYield;
    private String healthStatus;
    private Integer beeActivity;
    private BigDecimal temperature;
    private BigDecimal humidity;
    private BigDecimal appliedAdjustment;
    private String reason;

    public YieldPredictionExplanation() {
    }

    public YieldPredictionExplanation(int historicalDataPoints, BigDecimal averageHistoricalYield, String healthStatus,
                                        Integer beeActivity, BigDecimal temperature, BigDecimal humidity,
                                        BigDecimal appliedAdjustment, String reason) {
        this.historicalDataPoints = historicalDataPoints;
        this.averageHistoricalYield = averageHistoricalYield;
        this.healthStatus = healthStatus;
        this.beeActivity = beeActivity;
        this.temperature = temperature;
        this.humidity = humidity;
        this.appliedAdjustment = appliedAdjustment;
        this.reason = reason;
    }

    public int getHistoricalDataPoints() {
        return historicalDataPoints;
    }

    public void setHistoricalDataPoints(int historicalDataPoints) {
        this.historicalDataPoints = historicalDataPoints;
    }

    public BigDecimal getAverageHistoricalYield() {
        return averageHistoricalYield;
    }

    public void setAverageHistoricalYield(BigDecimal averageHistoricalYield) {
        this.averageHistoricalYield = averageHistoricalYield;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }

    public Integer getBeeActivity() {
        return beeActivity;
    }

    public void setBeeActivity(Integer beeActivity) {
        this.beeActivity = beeActivity;
    }

    public BigDecimal getTemperature() {
        return temperature;
    }

    public void setTemperature(BigDecimal temperature) {
        this.temperature = temperature;
    }

    public BigDecimal getHumidity() {
        return humidity;
    }

    public void setHumidity(BigDecimal humidity) {
        this.humidity = humidity;
    }

    public BigDecimal getAppliedAdjustment() {
        return appliedAdjustment;
    }

    public void setAppliedAdjustment(BigDecimal appliedAdjustment) {
        this.appliedAdjustment = appliedAdjustment;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
