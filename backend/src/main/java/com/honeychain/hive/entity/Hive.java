package com.honeychain.hive.entity;

import com.honeychain.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDate;

@Entity
@Table(name = "hives", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "hive_code" })
})
public class Hive extends BaseEntity {

    @Column(name = "beekeeper_profile_id", nullable = false)
    private Long beekeeperProfileId;

    @Column(name = "hive_code", nullable = false, unique = true, length = 20)
    private String hiveCode;

    @Column(name = "cluster_name", nullable = false, length = 100)
    private String clusterName;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private HiveStatus status = HiveStatus.ACTIVE;

    @Column(name = "installed_date", nullable = false)
    private LocalDate installedDate;

    public Hive() {
    }

    public Hive(Long beekeeperProfileId, String hiveCode, String clusterName,
            Double latitude, Double longitude, HiveStatus status, LocalDate installedDate) {
        this.beekeeperProfileId = beekeeperProfileId;
        this.hiveCode = hiveCode;
        this.clusterName = clusterName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status != null ? status : HiveStatus.ACTIVE;
        this.installedDate = installedDate;
    }

    public Long getBeekeeperProfileId() {
        return beekeeperProfileId;
    }

    public void setBeekeeperProfileId(Long beekeeperProfileId) {
        this.beekeeperProfileId = beekeeperProfileId;
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
}
