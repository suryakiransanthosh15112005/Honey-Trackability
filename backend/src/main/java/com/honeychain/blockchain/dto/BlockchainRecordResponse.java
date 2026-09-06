package com.honeychain.blockchain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.honeychain.blockchain.entity.BlockchainRecordType;

import java.time.LocalDateTime;

public class BlockchainRecordResponse {

    private boolean recorded;
    private String batchId;
    private String dataHash;
    private String transactionHash;
    private Long blockNumber;
    private String network;
    private BlockchainRecordType recordType;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime recordedAt;

    public BlockchainRecordResponse() {
    }

    public BlockchainRecordResponse(boolean recorded, String batchId, String dataHash, String transactionHash,
            Long blockNumber, String network, BlockchainRecordType recordType, LocalDateTime recordedAt) {
        this.recorded = recorded;
        this.batchId = batchId;
        this.dataHash = dataHash;
        this.transactionHash = transactionHash;
        this.blockNumber = blockNumber;
        this.network = network;
        this.recordType = recordType;
        this.recordedAt = recordedAt;
    }

    public boolean isRecorded() {
        return recorded;
    }

    public void setRecorded(boolean recorded) {
        this.recorded = recorded;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getDataHash() {
        return dataHash;
    }

    public void setDataHash(String dataHash) {
        this.dataHash = dataHash;
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

    public BlockchainRecordType getRecordType() {
        return recordType;
    }

    public void setRecordType(BlockchainRecordType recordType) {
        this.recordType = recordType;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }
}
