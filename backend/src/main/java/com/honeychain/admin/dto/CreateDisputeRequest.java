package com.honeychain.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateDisputeRequest {

    @NotBlank(message = "Batch ID is required")
    @Size(max = 64, message = "Batch ID must not exceed 64 characters")
    private String batchId;

    @Size(max = 50, message = "Order number must not exceed 50 characters")
    private String orderNumber;

    @NotBlank(message = "Reason is required")
    @Size(max = 200, message = "Reason must not exceed 200 characters")
    private String reason;

    @Size(max = 1500, message = "Description must not exceed 1500 characters")
    private String description;

    public CreateDisputeRequest() {
    }

    public CreateDisputeRequest(String batchId, String orderNumber, String reason, String description) {
        this.batchId = batchId;
        this.orderNumber = orderNumber;
        this.reason = reason;
        this.description = description;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
