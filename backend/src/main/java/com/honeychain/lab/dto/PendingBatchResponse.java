package com.honeychain.lab.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PendingBatchResponse {

    private String batchId;
    private String beekeeperName;
    private String village;
    private String hiveCode;
    private String clusterName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate harvestDate;

    private BigDecimal quantityKg;
    private String photoUrl;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime submittedAt;

    public PendingBatchResponse() {
    }

    public PendingBatchResponse(String batchId, String beekeeperName, String village, String hiveCode,
                                String clusterName, LocalDate harvestDate, BigDecimal quantityKg,
                                String photoUrl, LocalDateTime submittedAt) {
        this.batchId = batchId;
        this.beekeeperName = beekeeperName;
        this.village = village;
        this.hiveCode = hiveCode;
        this.clusterName = clusterName;
        this.harvestDate = harvestDate;
        this.quantityKg = quantityKg;
        this.photoUrl = photoUrl;
        this.submittedAt = submittedAt;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
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

    public LocalDate getHarvestDate() {
        return harvestDate;
    }

    public void setHarvestDate(LocalDate harvestDate) {
        this.harvestDate = harvestDate;
    }

    public BigDecimal getQuantityKg() {
        return quantityKg;
    }

    public void setQuantityKg(BigDecimal quantityKg) {
        this.quantityKg = quantityKg;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
}
