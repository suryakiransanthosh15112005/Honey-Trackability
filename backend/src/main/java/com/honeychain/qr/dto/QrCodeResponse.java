package com.honeychain.qr.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class QrCodeResponse {

    private String batchId;
    private String qrValue;
    private String qrImageUrl;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime generatedAt;

    public QrCodeResponse() {
    }

    public QrCodeResponse(String batchId, String qrValue, String qrImageUrl, LocalDateTime generatedAt) {
        this.batchId = batchId;
        this.qrValue = qrValue;
        this.qrImageUrl = qrImageUrl;
        this.generatedAt = generatedAt;
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
