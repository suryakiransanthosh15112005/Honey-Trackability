package com.honeychain.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.honeychain.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(name = "phone_number", unique = true, nullable = false, length = 30)
    private String phoneNumber;

    @JsonIgnore
    @Column(name = "password", length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private Role role;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public User() {
    }

    public User(String phoneNumber, String password, Role role) {
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.role = role;
        this.isActive = true;
    }

    public User(String phoneNumber, String password, Role role, Boolean isActive) {
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.role = role;
        this.isActive = isActive != null ? isActive : true;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
