package com.honeychain.qr.entity;

import com.honeychain.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

@Entity
@Table(name = "qr_codes", uniqueConstraints = {
        @UniqueConstraint(name = "uk_qr_codes_batch_id", columnNames = { "batch_id" })
}, indexes = {
        @Index(name = "idx_qr_codes_batch_id", columnList = "batch_id")
})
public class QrCode extends BaseEntity {

    @Column(name = "batch_id", nullable = false, unique = true, length = 30)
    private String batchId;

    @Column(name = "qr_value", nullable = false, length = 500)
    private String qrValue;

    @Column(name = "qr_image_url", nullable = false, length = 255)
    private String qrImageUrl;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    public QrCode() {
    }

    public QrCode(String batchId, String qrValue, String qrImageUrl, LocalDateTime generatedAt) {
        this.batchId = batchId;
        this.qrValue = qrValue;
        this.qrImageUrl = qrImageUrl;
        this.generatedAt = generatedAt != null ? generatedAt : LocalDateTime.now();
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getQrValue() {
        return qrValue;
    }

    public void setQrValue(String qrValue) {
        this.qrValue = qrValue;
    }

    public String getQrImageUrl() {
        return qrImageUrl;
    }

    public void setQrImageUrl(String qrImageUrl) {
        this.qrImageUrl = qrImageUrl;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
}
