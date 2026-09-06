package com.honeychain.customer.dto;

public class CustomerProfileStatusResponse {

    private boolean profileExists;
    private boolean isComplete;
    private String message;

    public CustomerProfileStatusResponse() {
    }

    public CustomerProfileStatusResponse(boolean profileExists, boolean isComplete, String message) {
        this.profileExists = profileExists;
        this.isComplete = isComplete;
        this.message = message;
    }

    public boolean isProfileExists() {
        return profileExists;
    }

    public void setProfileExists(boolean profileExists) {
        this.profileExists = profileExists;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
