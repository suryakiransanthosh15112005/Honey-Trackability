package com.honeychain.user.entity;

public enum Role {
    BEEKEEPER,
    CUSTOMER,
    LAB,
    KVIC_OFFICER,
    ADMIN;

    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
