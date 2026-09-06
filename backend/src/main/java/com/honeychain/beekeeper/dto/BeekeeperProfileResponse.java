package com.honeychain.beekeeper.dto;

import com.honeychain.beekeeper.entity.BeekeeperVerificationStatus;
import com.honeychain.beekeeper.entity.PreferredLanguage;

import java.time.LocalDateTime;

public class BeekeeperProfileResponse {

    private Long id;
    private Long userId;
    private String phoneNumber;
    private String kvicId;
    private String name;
    private String village;
    private String photoUrl;
    private Double latitude;
    private Double longitude;
    private PreferredLanguage preferredLanguage;
    private BeekeeperVerificationStatus verificationStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BeekeeperProfileResponse() {
    }

    public BeekeeperProfileResponse(Long id, Long userId, String phoneNumber, String kvicId,
            String name, String village, String photoUrl, Double latitude,
            Double longitude, PreferredLanguage preferredLanguage,
            BeekeeperVerificationStatus verificationStatus,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.kvicId = kvicId;
        this.name = name;
        this.village = village;
        this.photoUrl = photoUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.preferredLanguage = preferredLanguage;
        this.verificationStatus = verificationStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
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
