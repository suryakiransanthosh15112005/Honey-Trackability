package com.honeychain.iot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.honeychain.iot.entity.HiveHealthStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class HiveHealthResponse {

    private Long hiveId;
    private String hiveCode;
    private String clusterName;
    private HiveHealthStatus status;
    private String message;
    private BigDecimal temperature;
    private BigDecimal humidity;
    private Integer beeActivity;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime checkedAt;

    /** True when no sensor data is available yet */
    private boolean noDataAvailable;

    public HiveHealthResponse() {
    }

    public Long getHiveId() { return hiveId; }
    public void setHiveId(Long hiveId) { this.hiveId = hiveId; }
    public String getHiveCode() { return hiveCode; }
    public void setHiveCode(String hiveCode) { this.hiveCode = hiveCode; }
    public String getClusterName() { return clusterName; }
    public void setClusterName(String clusterName) { this.clusterName = clusterName; }
    public HiveHealthStatus getStatus() { return status; }
    public void setStatus(HiveHealthStatus status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public BigDecimal getTemperature() { return temperature; }
    public void setTemperature(BigDecimal temperature) { this.temperature = temperature; }
    public BigDecimal getHumidity() { return humidity; }
    public void setHumidity(BigDecimal humidity) { this.humidity = humidity; }
    public Integer getBeeActivity() { return beeActivity; }
    public void setBeeActivity(Integer beeActivity) { this.beeActivity = beeActivity; }
    public LocalDateTime getCheckedAt() { return checkedAt; }
    public void setCheckedAt(LocalDateTime checkedAt) { this.checkedAt = checkedAt; }
    public boolean isNoDataAvailable() { return noDataAvailable; }
    public void setNoDataAvailable(boolean noDataAvailable) { this.noDataAvailable = noDataAvailable; }
}
