package com.honeychain.marketplace.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ProductUpdateRequest {

    @NotBlank(message = "Product name is required")
    @Size(max = 120, message = "Product name cannot exceed 120 characters")
    private String productName;

    @NotBlank(message = "Flower source is required")
    @Size(max = 60, message = "Flower source cannot exceed 60 characters")
    private String flowerSource;

    @NotBlank(message = "Region is required")
    @Size(max = 60, message = "Region cannot exceed 60 characters")
    private String region;

    @NotNull(message = "Price per kg is required")
    @DecimalMin(value = "0.01", message = "Price per kg must be greater than 0")
    private BigDecimal pricePerKg;

    @NotNull(message = "Available quantity is required")
    @DecimalMin(value = "0.01", message = "Available quantity must be greater than 0")
    private BigDecimal availableQuantityKg;

    @Size(max = 1500, message = "Description cannot exceed 1500 characters")
    private String description;

    private String imageUrl;

    public ProductUpdateRequest() {
    }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getFlowerSource() { return flowerSource; }
    public void setFlowerSource(String flowerSource) { this.flowerSource = flowerSource; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public BigDecimal getPricePerKg() { return pricePerKg; }
    public void setPricePerKg(BigDecimal pricePerKg) { this.pricePerKg = pricePerKg; }
    public BigDecimal getAvailableQuantityKg() { return availableQuantityKg; }
    public void setAvailableQuantityKg(BigDecimal availableQuantityKg) { this.availableQuantityKg = availableQuantityKg; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
