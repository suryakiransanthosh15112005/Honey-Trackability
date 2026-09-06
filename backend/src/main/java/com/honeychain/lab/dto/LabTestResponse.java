package com.honeychain.lab.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.honeychain.blockchain.dto.BlockchainRecordResponse;
import com.honeychain.lab.entity.LabTestResult;

import java.time.LocalDateTime;

public class LabTestResponse {

    private Long id;
    private String batchId;
    private Long labUserId;
    private Integer purityScore;
    private LabTestResult result;
    private String certificateUrl;
    private String remarks;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime testedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    private BlockchainRecordResponse blockchainRecord;

    public LabTestResponse() {
    }

    public LabTestResponse(Long id, String batchId, Long labUserId, Integer purityScore, LabTestResult result,
                           String certificateUrl, String remarks, LocalDateTime testedAt, LocalDateTime createdAt,
                           BlockchainRecordResponse blockchainRecord) {
        this.id = id;
        this.batchId = batchId;
        this.labUserId = labUserId;
        this.purityScore = purityScore;
        this.result = result;
        this.certificateUrl = certificateUrl;
        this.remarks = remarks;
        this.testedAt = testedAt;
        this.createdAt = createdAt;
        this.blockchainRecord = blockchainRecord;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public Long getLabUserId() {
        return labUserId;
    }

    public void setLabUserId(Long labUserId) {
        this.labUserId = labUserId;
    }

    public Integer getPurityScore() {
        return purityScore;
    }

    public void setPurityScore(Integer purityScore) {
        this.purityScore = purityScore;
    }

    public LabTestResult getResult() {
        return result;
    }

    public void setResult(LabTestResult result) {
        this.result = result;
    }

    public String getCertificateUrl() {
        return certificateUrl;
    }

    public void setCertificateUrl(String certificateUrl) {
        this.certificateUrl = certificateUrl;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getTestedAt() {
        return testedAt;
    }

    public void setTestedAt(LocalDateTime testedAt) {
        this.testedAt = testedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public BlockchainRecordResponse getBlockchainRecord() {
        return blockchainRecord;
    }

    public void setBlockchainRecord(BlockchainRecordResponse blockchainRecord) {
        this.blockchainRecord = blockchainRecord;
    }
}
