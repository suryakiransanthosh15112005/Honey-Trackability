package com.honeychain.cart.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class UpdateCartItemRequest {

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.01", message = "Quantity must be at least 0.01 kg")
    private BigDecimal quantityKg;

    public UpdateCartItemRequest() {}

    public BigDecimal getQuantityKg() { return quantityKg; }
    public void setQuantityKg(BigDecimal quantityKg) { this.quantityKg = quantityKg; }
}
