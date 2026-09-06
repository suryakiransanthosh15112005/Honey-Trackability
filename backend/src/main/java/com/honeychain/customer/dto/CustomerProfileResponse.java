package com.honeychain.customer.dto;

import com.honeychain.customer.entity.CustomerProfile;

import java.time.LocalDateTime;

public class CustomerProfileResponse {

    private Long id;
    private Long userId;
    private String phoneNumber;
    private String fullName;
    private String email;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String profilePhotoUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CustomerProfileResponse() {
    }

    public CustomerProfileResponse(Long id, Long userId, String phoneNumber, String fullName, String email, String address, String city, String state, String pincode, String profilePhotoUrl, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.fullName = fullName;
        this.email = email;
        this.address = address;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
        this.profilePhotoUrl = profilePhotoUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CustomerProfileResponse fromEntity(CustomerProfile profile, String phoneNumber) {
        if (profile == null) return null;
        return new CustomerProfileResponse(
                profile.getId(),
                profile.getUserId(),
                phoneNumber,
                profile.getFullName(),
                profile.getEmail(),
                profile.getAddress(),
                profile.getCity(),
                profile.getState(),
                profile.getPincode(),
                profile.getProfilePhotoUrl(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
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
