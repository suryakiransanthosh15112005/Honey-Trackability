package com.honeychain.batch.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public class HoneyBatchUpdateRequest {

    @NotNull(message = "Harvest date is required")
    @PastOrPresent(message = "Harvest date cannot be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate harvestDate;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
    @DecimalMax(value = "10000.00", message = "Quantity cannot exceed 10,000 kg")
    private BigDecimal quantityKg;

    public HoneyBatchUpdateRequest() {
    }

    public HoneyBatchUpdateRequest(LocalDate harvestDate, BigDecimal quantityKg) {
        this.harvestDate = harvestDate;
        this.quantityKg = quantityKg;
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
}
