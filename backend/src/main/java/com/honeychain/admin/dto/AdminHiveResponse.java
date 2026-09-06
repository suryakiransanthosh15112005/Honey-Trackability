package com.honeychain.admin.dto;

import com.honeychain.hive.entity.HiveStatus;
import com.honeychain.iot.entity.HiveHealthStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AdminHiveResponse {

    private Long id;
    private String hiveCode;
    private Long beekeeperProfileId;
    private String beekeeperName;
    private String village;
    private String clusterName;
    private LocalDate installedDate;
    private Double latitude;
    private Double longitude;
    private HiveStatus status;
    private HiveHealthStatus healthStatus;
    private Double lastTemperature;
    private Double lastHumidity;
    private Integer lastBeeActivity;
    private LocalDateTime lastSensorReadingTime;

    public AdminHiveResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHiveCode() {
        return hiveCode;
    }

    public void setHiveCode(String hiveCode) {
        this.hiveCode = hiveCode;
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

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public LocalDate getInstalledDate() {
        return installedDate;
    }

    public void setInstalledDate(LocalDate installedDate) {
        this.installedDate = installedDate;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public HiveStatus getStatus() {
        return status;
    }

    public void setStatus(HiveStatus status) {
        this.status = status;
    }

    public HiveHealthStatus getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(HiveHealthStatus healthStatus) {
        this.healthStatus = healthStatus;
    }

    public Double getLastTemperature() {
        return lastTemperature;
    }

    public void setLastTemperature(Double lastTemperature) {
        this.lastTemperature = lastTemperature;
    }

    public Double getLastHumidity() {
        return lastHumidity;
    }

    public void setLastHumidity(Double lastHumidity) {
        this.lastHumidity = lastHumidity;
    }

    public Integer getLastBeeActivity() {
        return lastBeeActivity;
    }

    public void setLastBeeActivity(Integer lastBeeActivity) {
        this.lastBeeActivity = lastBeeActivity;
    }

    public LocalDateTime getLastSensorReadingTime() {
        return lastSensorReadingTime;
    }

    public void setLastSensorReadingTime(LocalDateTime lastSensorReadingTime) {
        this.lastSensorReadingTime = lastSensorReadingTime;
    }
}
