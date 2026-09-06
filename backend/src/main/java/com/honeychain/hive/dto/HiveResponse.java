package com.honeychain.hive.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.honeychain.hive.entity.HiveStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class HiveResponse {

    private Long id;
    private String hiveCode;
    private String clusterName;
    private Double latitude;
    private Double longitude;
    private HiveStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate installedDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    public HiveResponse() {
    }

    public HiveResponse(Long id, String hiveCode, String clusterName, Double latitude, Double longitude,
            HiveStatus status, LocalDate installedDate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.hiveCode = hiveCode;
        this.clusterName = clusterName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.installedDate = installedDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
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

    public LocalDate getInstalledDate() {
        return installedDate;
    }

    public void setInstalledDate(LocalDate installedDate) {
        this.installedDate = installedDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
