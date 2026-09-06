package com.honeychain.marketplace.entity;

import com.honeychain.batch.entity.HoneyBatch;
import com.honeychain.beekeeper.entity.BeekeeperProfile;
import com.honeychain.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;

@Entity
@Table(name = "products", uniqueConstraints = {
        @UniqueConstraint(name = "uk_products_batch_id", columnNames = { "batch_id" })
}, indexes = {
        @Index(name = "idx_products_batch_id", columnList = "batch_id"),
        @Index(name = "idx_products_beekeeper_profile_id", columnList = "beekeeper_profile_id"),
        @Index(name = "idx_products_region", columnList = "region"),
        @Index(name = "idx_products_flower_source", columnList = "flower_source"),
        @Index(name = "idx_products_is_active", columnList = "is_active"),
        @Index(name = "idx_products_created_at", columnList = "created_at")
})
public class Product extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "batch_id", nullable = false, unique = true)
    private HoneyBatch batch;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "beekeeper_profile_id", nullable = false)
    private BeekeeperProfile beekeeperProfile;

    @Column(name = "product_name", nullable = false, length = 120)
    private String productName;

    @Column(name = "flower_source", nullable = false, length = 60)
    private String flowerSource;

    @Column(name = "region", nullable = false, length = 60)
    private String region;

    @Column(name = "price_per_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerKg;

    @Column(name = "available_quantity_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal availableQuantityKg;

    @Column(name = "description", length = 1500)
    private String description;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "rating", nullable = false, precision = 3, scale = 2)
    private BigDecimal rating = BigDecimal.ZERO;

    @Column(name = "review_count", nullable = false)
    private Integer reviewCount = 0;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public Product() {
    }

    public Product(HoneyBatch batch, BeekeeperProfile beekeeperProfile, String productName, String flowerSource,
            String region, BigDecimal pricePerKg, BigDecimal availableQuantityKg, String description,
            String imageUrl) {
        this.batch = batch;
        this.beekeeperProfile = beekeeperProfile;
        this.productName = productName;
        this.flowerSource = flowerSource;
        this.region = region;
        this.pricePerKg = pricePerKg;
        this.availableQuantityKg = availableQuantityKg;
        this.description = description;
        this.imageUrl = imageUrl;
        this.rating = BigDecimal.ZERO;
        this.reviewCount = 0;
        this.isActive = true;
    }

    public HoneyBatch getBatch() {
        return batch;
    }

    public void setBatch(HoneyBatch batch) {
        this.batch = batch;
    }

    public BeekeeperProfile getBeekeeperProfile() {
        return beekeeperProfile;
    }

    public void setBeekeeperProfile(BeekeeperProfile beekeeperProfile) {
        this.beekeeperProfile = beekeeperProfile;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getFlowerSource() {
        return flowerSource;
    }

    public void setFlowerSource(String flowerSource) {
        this.flowerSource = flowerSource;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public BigDecimal getPricePerKg() {
        return pricePerKg;
    }

    public void setPricePerKg(BigDecimal pricePerKg) {
        this.pricePerKg = pricePerKg;
    }

    public BigDecimal getAvailableQuantityKg() {
        return availableQuantityKg;
    }

    public void setAvailableQuantityKg(BigDecimal availableQuantityKg) {
        this.availableQuantityKg = availableQuantityKg;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }
}
