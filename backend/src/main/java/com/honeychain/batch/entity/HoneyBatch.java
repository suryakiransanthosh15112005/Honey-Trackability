package com.honeychain.batch.entity;

import com.honeychain.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "honey_batches",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_honey_batches_batch_id", columnNames = {"batch_id"})
        },
        indexes = {
                @Index(name = "idx_batches_batch_id", columnList = "batch_id"),
                @Index(name = "idx_batches_beekeeper_profile_id", columnList = "beekeeper_profile_id"),
                @Index(name = "idx_batches_hive_id", columnList = "hive_id"),
                @Index(name = "idx_batches_status", columnList = "status")
        }
)
public class HoneyBatch extends BaseEntity {

    @Column(name = "batch_id", nullable = false, unique = true, length = 30)
    private String batchId;

    @Column(name = "beekeeper_profile_id", nullable = false)
    private Long beekeeperProfileId;

    @Column(name = "hive_id", nullable = false)
    private Long hiveId;

    @Column(name = "harvest_date", nullable = false)
    private LocalDate harvestDate;

    @Column(name = "quantity_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantityKg;

    @Column(name = "photo_url", length = 255)
    private String photoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private BatchStatus status = BatchStatus.CREATED;

    public HoneyBatch() {
    }

    public HoneyBatch(String batchId, Long beekeeperProfileId, Long hiveId,
                      LocalDate harvestDate, BigDecimal quantityKg, String photoUrl, BatchStatus status) {
        this.batchId = batchId;
        this.beekeeperProfileId = beekeeperProfileId;
        this.hiveId = hiveId;
        this.harvestDate = harvestDate;
        this.quantityKg = quantityKg;
        this.photoUrl = photoUrl;
        this.status = status != null ? status : BatchStatus.CREATED;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public Long getBeekeeperProfileId() {
        return beekeeperProfileId;
    }

    public void setBeekeeperProfileId(Long beekeeperProfileId) {
        this.beekeeperProfileId = beekeeperProfileId;
    }

    public Long getHiveId() {
        return hiveId;
    }

    public void setHiveId(Long hiveId) {
        this.hiveId = hiveId;
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
}
