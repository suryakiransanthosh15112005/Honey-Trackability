package com.honeychain.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "ai.yield-prediction")
public class AiProperties {

    private long maxAgeHours = 24;
    private BigDecimal defaultMinKg = new BigDecimal("5.00");
    private BigDecimal defaultMaxKg = new BigDecimal("8.00");
    private BigDecimal healthyAdjustment = new BigDecimal("1.10");
    private BigDecimal watchAdjustment = new BigDecimal("0.95");
    private BigDecimal alertAdjustment = new BigDecimal("0.75");
    private int healthyDays = 15;
    private int watchDays = 18;
    private int alertDays = 22;

    public long getMaxAgeHours() {
        return maxAgeHours;
    }

    public void setMaxAgeHours(long maxAgeHours) {
        this.maxAgeHours = maxAgeHours;
    }

    public BigDecimal getDefaultMinKg() {
        return defaultMinKg;
    }

    public void setDefaultMinKg(BigDecimal defaultMinKg) {
        this.defaultMinKg = defaultMinKg;
    }

    public BigDecimal getDefaultMaxKg() {
        return defaultMaxKg;
    }

    public void setDefaultMaxKg(BigDecimal defaultMaxKg) {
        this.defaultMaxKg = defaultMaxKg;
    }

    public BigDecimal getHealthyAdjustment() {
        return healthyAdjustment;
    }

    public void setHealthyAdjustment(BigDecimal healthyAdjustment) {
        this.healthyAdjustment = healthyAdjustment;
    }

    public BigDecimal getWatchAdjustment() {
        return watchAdjustment;
    }

    public void setWatchAdjustment(BigDecimal watchAdjustment) {
        this.watchAdjustment = watchAdjustment;
    }

    public BigDecimal getAlertAdjustment() {
        return alertAdjustment;
    }

    public void setAlertAdjustment(BigDecimal alertAdjustment) {
        this.alertAdjustment = alertAdjustment;
    }

    public int getHealthyDays() {
        return healthyDays;
    }

    public void setHealthyDays(int healthyDays) {
        this.healthyDays = healthyDays;
    }

    public int getWatchDays() {
        return watchDays;
    }

    public void setWatchDays(int watchDays) {
        this.watchDays = watchDays;
    }

    public int getAlertDays() {
        return alertDays;
    }

    public void setAlertDays(int alertDays) {
        this.alertDays = alertDays;
    }
}
