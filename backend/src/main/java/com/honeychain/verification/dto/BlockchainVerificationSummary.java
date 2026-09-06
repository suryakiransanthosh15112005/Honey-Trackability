package com.honeychain.verification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class BlockchainVerificationSummary {

    private boolean verified;
    private String network;
    private String transactionHash;
    private Long blockNumber;
    private String dataHash;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime recordedAt;

    public BlockchainVerificationSummary() {
    }

    public BlockchainVerificationSummary(boolean verified, String network, String transactionHash,
            Long blockNumber, String dataHash, LocalDateTime recordedAt) {
        this.verified = verified;
        this.network = network;
        this.transactionHash = transactionHash;
        this.blockNumber = blockNumber;
        this.dataHash = dataHash;
        this.recordedAt = recordedAt;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public Long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(Long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getDataHash() {
        return dataHash;
    }

    public void setDataHash(String dataHash) {
        this.dataHash = dataHash;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }
}
