package com.honeychain.admin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.honeychain.beekeeper.entity.BeekeeperVerificationStatus;
import com.honeychain.beekeeper.entity.PreferredLanguage;

import java.time.LocalDateTime;

public class AdminBeekeeperResponse {

    private Long id;
    private Long userId;
    private String phoneNumber;
    private String kvicId;
    private String name;
    private String village;
    private String photoUrl;
    private PreferredLanguage preferredLanguage;
    private BeekeeperVerificationStatus verificationStatus;
    private long hiveCount;
    private long batchCount;
    private long productCount;
    private Double averageRating;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public AdminBeekeeperResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getKvicId() {
        return kvicId;
    }

    public void setKvicId(String kvicId) {
        this.kvicId = kvicId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public PreferredLanguage getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(PreferredLanguage preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public BeekeeperVerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(BeekeeperVerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public long getHiveCount() {
        return hiveCount;
    }

    public void setHiveCount(long hiveCount) {
        this.hiveCount = hiveCount;
    }

    public long getBatchCount() {
        return batchCount;
    }

    public void setBatchCount(long batchCount) {
        this.batchCount = batchCount;
    }

    public long getProductCount() {
        return productCount;
    }

    public void setProductCount(long productCount) {
        this.productCount = productCount;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
