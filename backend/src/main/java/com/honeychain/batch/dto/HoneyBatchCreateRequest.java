package com.honeychain.batch.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public class HoneyBatchCreateRequest {

    @NotNull(message = "Hive ID is required")
    private Long hiveId;

    @NotNull(message = "Harvest date is required")
    @PastOrPresent(message = "Harvest date cannot be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate harvestDate;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
    @DecimalMax(value = "10000.00", message = "Quantity cannot exceed 10,000 kg")
    private BigDecimal quantityKg;

    /**
     * Optional client-generated idempotency key for offline batch synchronization.
     * Sent via X-Idempotency-Key header and injected here by the controller.
     * Not validated, not persisted in HoneyBatch — only used for idempotency
     * lookup.
     * Example value: "LOCAL-5f8c3a2b1d4e6f7a8b9c0d1e"
     */
    private String idempotencyKey;

    public HoneyBatchCreateRequest() {
    }

    public HoneyBatchCreateRequest(Long hiveId, LocalDate harvestDate, BigDecimal quantityKg) {
        this.hiveId = hiveId;
        this.harvestDate = harvestDate;
        this.quantityKg = quantityKg;
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

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }
}
