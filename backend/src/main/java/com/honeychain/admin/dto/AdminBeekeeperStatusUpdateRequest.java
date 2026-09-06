package com.honeychain.admin.dto;

import com.honeychain.beekeeper.entity.BeekeeperVerificationStatus;
import jakarta.validation.constraints.NotNull;

public class AdminBeekeeperStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private BeekeeperVerificationStatus status;

    public AdminBeekeeperStatusUpdateRequest() {
    }

    public AdminBeekeeperStatusUpdateRequest(BeekeeperVerificationStatus status) {
        this.status = status;
    }

    public BeekeeperVerificationStatus getStatus() {
        return status;
    }

    public void setStatus(BeekeeperVerificationStatus status) {
        this.status = status;
    }
}
