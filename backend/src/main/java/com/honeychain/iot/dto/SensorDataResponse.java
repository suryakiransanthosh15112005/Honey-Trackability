package com.honeychain.iot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SensorDataResponse {

    private Long hiveId;
    private String hiveCode;
    private BigDecimal temperature;
    private BigDecimal humidity;
    private Integer beeActivity;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime recordedAt;

    public SensorDataResponse() {
    }

    public SensorDataResponse(Long hiveId, String hiveCode, BigDecimal temperature,
            BigDecimal humidity, Integer beeActivity, LocalDateTime recordedAt) {
        this.hiveId = hiveId;
        this.hiveCode = hiveCode;
        this.temperature = temperature;
        this.humidity = humidity;
        this.beeActivity = beeActivity;
        this.recordedAt = recordedAt;
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

    public Integer getBeeActivity() {
        return beeActivity;
    }

    public void setBeeActivity(Integer beeActivity) {
        this.beeActivity = beeActivity;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }
}
