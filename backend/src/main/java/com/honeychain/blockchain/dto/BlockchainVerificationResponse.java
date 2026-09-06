package com.honeychain.blockchain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class BlockchainVerificationResponse {

    private boolean verified;
    private String batchId;
    private String storedHash;
    private String calculatedHash;
    private String transactionHash;
    private Long blockNumber;
    private String network;
    private String message;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime verifiedAt;

    public BlockchainVerificationResponse() {
        this.verifiedAt = LocalDateTime.now();
    }

    public BlockchainVerificationResponse(boolean verified, String batchId, String storedHash, String calculatedHash,
            String transactionHash, Long blockNumber, String network, String message) {
        this.verified = verified;
        this.batchId = batchId;
        this.storedHash = storedHash;
        this.calculatedHash = calculatedHash;
        this.transactionHash = transactionHash;
        this.blockNumber = blockNumber;
        this.network = network;
        this.message = message;
        this.verifiedAt = LocalDateTime.now();
    }

    public static BlockchainVerificationResponse success(String batchId, String storedHash, String calculatedHash,
            String transactionHash, Long blockNumber, String network) {
        return new BlockchainVerificationResponse(
                true,
                batchId,
                storedHash,
                calculatedHash,
                transactionHash,
                blockNumber,
                network,
                "Batch data successfully verified against immutable blockchain record");
    }

    public static BlockchainVerificationResponse tampered(String batchId, String storedHash, String calculatedHash,
            String transactionHash, Long blockNumber, String network) {
        return new BlockchainVerificationResponse(
                false,
                batchId,
                storedHash,
                calculatedHash,
                transactionHash,
                blockNumber,
                network,
                "Batch data has been modified and does not match the immutable blockchain record");
    }

    public static BlockchainVerificationResponse notFound(String batchId) {
        return new BlockchainVerificationResponse(
                false,
                batchId,
                null,
                null,
                null,
                null,
                null,
                "No blockchain record found for this batch");
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getStoredHash() {
        return storedHash;
    }

    public void setStoredHash(String storedHash) {
        this.storedHash = storedHash;
    }

    public String getCalculatedHash() {
        return calculatedHash;
    }

    public void setCalculatedHash(String calculatedHash) {
        this.calculatedHash = calculatedHash;
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

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }
}
