package com.honeychain.beekeeper.entity;

import com.honeychain.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "beekeeper_profiles", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id" }),
        @UniqueConstraint(columnNames = { "kvic_id" })
})
public class BeekeeperProfile extends BaseEntity {

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "kvic_id", nullable = false, unique = true, length = 50)
    private String kvicId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "village", nullable = false, length = 100)
    private String village;

    @Column(name = "photo_url", length = 255)
    private String photoUrl;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_language", nullable = false, length = 20)
    private PreferredLanguage preferredLanguage = PreferredLanguage.ENGLISH;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false, length = 20)
    private BeekeeperVerificationStatus verificationStatus = BeekeeperVerificationStatus.PENDING;

    public BeekeeperProfile() {
    }

    public BeekeeperProfile(Long userId, String kvicId, String name, String village,
            String photoUrl, Double latitude, Double longitude,
            PreferredLanguage preferredLanguage, BeekeeperVerificationStatus verificationStatus) {
        this.userId = userId;
        this.kvicId = kvicId;
        this.name = name;
        this.village = village;
        this.photoUrl = photoUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.preferredLanguage = preferredLanguage != null ? preferredLanguage : PreferredLanguage.ENGLISH;
        this.verificationStatus = verificationStatus != null ? verificationStatus : BeekeeperVerificationStatus.PENDING;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
}
