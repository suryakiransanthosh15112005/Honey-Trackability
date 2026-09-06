package com.honeychain.marketplace.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProductResponse {

    private Long id;
    private String batchId;
    private LocalDate harvestDate;
    private String batchStatus;
    private String productName;
    private String flowerSource;
    private String region;
    private BigDecimal pricePerKg;
    private BigDecimal availableQuantityKg;
    private String description;
    private String imageUrl;
    private BigDecimal rating;
    private Integer reviewCount;
    private Boolean verified;
    private Integer purityScore;
    private Boolean isActive;
    private BeekeeperSummary beekeeper;

    public ProductResponse() {
    }

    public ProductResponse(Long id, String batchId, LocalDate harvestDate, String batchStatus, String productName,
                           String flowerSource, String region, BigDecimal pricePerKg, BigDecimal availableQuantityKg,
                           String description, String imageUrl, BigDecimal rating, Integer reviewCount,
                           Boolean verified, Integer purityScore, Boolean isActive, BeekeeperSummary beekeeper) {
        this.id = id;
        this.batchId = batchId;
        this.harvestDate = harvestDate;
        this.batchStatus = batchStatus;
        this.productName = productName;
        this.flowerSource = flowerSource;
        this.region = region;
        this.pricePerKg = pricePerKg;
        this.availableQuantityKg = availableQuantityKg;
        this.description = description;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.verified = verified;
        this.purityScore = purityScore;
        this.isActive = isActive;
        this.beekeeper = beekeeper;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }
    public LocalDate getHarvestDate() { return harvestDate; }
    public void setHarvestDate(LocalDate harvestDate) { this.harvestDate = harvestDate; }
    public String getBatchStatus() { return batchStatus; }
    public void setBatchStatus(String batchStatus) { this.batchStatus = batchStatus; }
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
    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }
    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
    public Boolean getVerified() { return verified; }
    public void setVerified(Boolean verified) { this.verified = verified; }
    public Integer getPurityScore() { return purityScore; }
    public void setPurityScore(Integer purityScore) { this.purityScore = purityScore; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }
    public BeekeeperSummary getBeekeeper() { return beekeeper; }
    public void setBeekeeper(BeekeeperSummary beekeeper) { this.beekeeper = beekeeper; }

    public static class BeekeeperSummary {
        private String name;
        private String village;
        private String photoUrl;
        private String kvicId;

        public BeekeeperSummary() {
        }

        public BeekeeperSummary(String name, String village, String photoUrl, String kvicId) {
            this.name = name;
            this.village = village;
            this.photoUrl = photoUrl;
            this.kvicId = kvicId;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getVillage() { return village; }
        public void setVillage(String village) { this.village = village; }
        public String getPhotoUrl() { return photoUrl; }
        public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
        public String getKvicId() { return kvicId; }
        public void setKvicId(String kvicId) { this.kvicId = kvicId; }
    }
}
