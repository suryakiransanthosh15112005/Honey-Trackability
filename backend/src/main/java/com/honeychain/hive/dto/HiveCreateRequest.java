package com.honeychain.hive.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class HiveCreateRequest {

    @NotBlank(message = "Cluster name is required")
    @Size(max = 100, message = "Cluster name cannot exceed 100 characters")
    private String clusterName;

    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90.0")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90.0")
    private Double latitude;

    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180.0")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180.0")
    private Double longitude;

    @NotNull(message = "Installed date is required")
    @PastOrPresent(message = "Installed date cannot be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate installedDate;

    public HiveCreateRequest() {
    }

    public HiveCreateRequest(String clusterName, Double latitude, Double longitude, LocalDate installedDate) {
        this.clusterName = clusterName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.installedDate = installedDate;
    }

    public String getClusterName() { return clusterName; }
    public void setClusterName(String clusterName) { this.clusterName = clusterName; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public LocalDate getInstalledDate() { return installedDate; }
    public void setInstalledDate(LocalDate installedDate) { this.installedDate = installedDate; }
}
