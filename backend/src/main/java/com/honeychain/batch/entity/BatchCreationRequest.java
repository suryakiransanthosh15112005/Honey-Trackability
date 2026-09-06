package com.honeychain.batch.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

@Entity
@Table(name = "batch_creation_requests", uniqueConstraints = {
        @UniqueConstraint(name = "uk_bcr_key_beekeeper", columnNames = { "idempotency_key", "beekeeper_profile_id" })
})
public class BatchCreationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Client-generated idempotency key (e.g. LOCAL-5f8c...) */
    @Column(name = "idempotency_key", nullable = false, length = 100)
    private String idempotencyKey;

    /** The officially-created HoneyBatch batchId (e.g. HC-2026-A8F31D29) */
    @Column(name = "batch_id", nullable = false, length = 50)
    private String batchId;

    /** Scopes the key to the authenticated beekeeper */
    @Column(name = "beekeeper_profile_id", nullable = false)
    private Long beekeeperProfileId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public BatchCreationRequest() {
    }

    public BatchCreationRequest(String idempotencyKey, String batchId, Long beekeeperProfileId) {
        this.idempotencyKey = idempotencyKey;
        this.batchId = batchId;
        this.beekeeperProfileId = beekeeperProfileId;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
