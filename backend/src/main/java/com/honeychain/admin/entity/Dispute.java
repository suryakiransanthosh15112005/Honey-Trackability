package com.honeychain.admin.entity;

import com.honeychain.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "disputes", indexes = {
        @Index(name = "idx_disputes_batch_id", columnList = "batch_id"),
        @Index(name = "idx_disputes_order_number", columnList = "order_number"),
        @Index(name = "idx_disputes_status", columnList = "status"),
        @Index(name = "idx_disputes_created_at", columnList = "created_at")
})
public class Dispute extends BaseEntity {

    @Column(name = "batch_id", nullable = false, length = 64)
    private String batchId;

    @Column(name = "order_number", length = 50)
    private String orderNumber;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "reason", nullable = false, length = 200)
    private String reason;

    @Column(name = "description", length = 1500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private DisputeStatus status = DisputeStatus.OPEN;

    @Column(name = "resolution_notes", length = 1500)
    private String resolutionNotes;

    public Dispute() {
    }

    public Dispute(String batchId, String orderNumber, Long customerId, String reason, String description) {
        this.batchId = batchId;
        this.orderNumber = orderNumber;
        this.customerId = customerId;
        this.reason = reason;
        this.description = description;
        this.status = DisputeStatus.OPEN;
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

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
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
