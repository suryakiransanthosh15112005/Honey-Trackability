package com.honeychain.batch.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.honeychain.batch.entity.BatchStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class HoneyBatchResponse {

    private Long id;
    private String batchId;
    private Long hiveId;
    private String hiveCode;
    private String clusterName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate harvestDate;

    private BigDecimal quantityKg;
    private String photoUrl;
    private BatchStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    public HoneyBatchResponse() {
    }

    public HoneyBatchResponse(Long id, String batchId, Long hiveId, String hiveCode, String clusterName,
                              LocalDate harvestDate, BigDecimal quantityKg, String photoUrl, BatchStatus status,
                              LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.batchId = batchId;
        this.hiveId = hiveId;
        this.hiveCode = hiveCode;
        this.clusterName = clusterName;
        this.harvestDate = harvestDate;
        this.quantityKg = quantityKg;
        this.photoUrl = photoUrl;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
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

    public BatchStatus getStatus() {
        return status;
    }

    public void setStatus(BatchStatus status) {
        this.status = status;
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
