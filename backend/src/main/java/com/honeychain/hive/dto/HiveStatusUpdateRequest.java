package com.honeychain.hive.dto;

import com.honeychain.hive.entity.HiveStatus;
import jakarta.validation.constraints.NotNull;

public class HiveStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private HiveStatus status;

    public HiveStatusUpdateRequest() {
    }

    public HiveStatusUpdateRequest(HiveStatus status) {
        this.status = status;
    }

    public HiveStatus getStatus() {
        return status;
    }

    public void setStatus(HiveStatus status) {
        this.status = status;
    }
}
