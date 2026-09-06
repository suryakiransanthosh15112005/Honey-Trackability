package com.honeychain.admin.dto;

import com.honeychain.admin.entity.DisputeStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class DisputeStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private DisputeStatus status;

    @Size(max = 1500, message = "Resolution notes cannot exceed 1500 characters")
    private String resolutionNotes;

    public DisputeStatusUpdateRequest() {
    }

    public DisputeStatusUpdateRequest(DisputeStatus status, String resolutionNotes) {
        this.status = status;
        this.resolutionNotes = resolutionNotes;
    }

    public DisputeStatus getStatus() {
        return status;
    }

    public void setStatus(DisputeStatus status) {
        this.status = status;
    }

    public String getResolutionNotes() {
        return resolutionNotes;
    }

    public void setResolutionNotes(String resolutionNotes) {
        this.resolutionNotes = resolutionNotes;
    }
}
