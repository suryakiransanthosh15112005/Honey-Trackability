package com.honeychain.beekeeper.dto;

import com.honeychain.beekeeper.entity.BeekeeperVerificationStatus;

public class BeekeeperProfileStatusResponse {

    private boolean completed;
    private BeekeeperVerificationStatus verificationStatus;
    private String kvicId;
    private String name;

    public BeekeeperProfileStatusResponse() {
    }

    public BeekeeperProfileStatusResponse(boolean completed, BeekeeperVerificationStatus verificationStatus,
            String kvicId, String name) {
        this.completed = completed;
        this.verificationStatus = verificationStatus;
        this.kvicId = kvicId;
        this.name = name;
    }

    public static BeekeeperProfileStatusResponse notCompleted() {
        return new BeekeeperProfileStatusResponse(false, null, null, null);
    }

    public static BeekeeperProfileStatusResponse completed(BeekeeperVerificationStatus verificationStatus,
            String kvicId, String name) {
        return new BeekeeperProfileStatusResponse(true, verificationStatus, kvicId, name);
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public BeekeeperVerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(BeekeeperVerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
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
}
