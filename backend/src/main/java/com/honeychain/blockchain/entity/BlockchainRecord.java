package com.honeychain.blockchain.entity;

import com.honeychain.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

@Entity
@Table(name = "blockchain_records", uniqueConstraints = {
        @UniqueConstraint(name = "uk_blockchain_batch_record_type", columnNames = { "batch_id", "record_type" })
}, indexes = {
        @Index(name = "idx_blockchain_batch_id", columnList = "batch_id"),
        @Index(name = "idx_blockchain_tx_hash", columnList = "transaction_hash"),
        @Index(name = "idx_blockchain_record_type", columnList = "record_type")
})
public class BlockchainRecord extends BaseEntity {

    @Column(name = "batch_id", nullable = false, length = 30)
    private String batchId;

    @Column(name = "data_hash", nullable = false, length = 64)
    private String dataHash;

    @Column(name = "transaction_hash", nullable = false, unique = true, length = 66)
    private String transactionHash;

    @Column(name = "block_number", nullable = false)
    private Long blockNumber;

    @Column(name = "network", nullable = false, length = 50)
    private String network;

    @Enumerated(EnumType.STRING)
    @Column(name = "record_type", nullable = false, length = 30)
    private BlockchainRecordType recordType;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    public BlockchainRecord() {
    }

    public BlockchainRecord(String batchId, String dataHash, String transactionHash,
            Long blockNumber, String network, BlockchainRecordType recordType, LocalDateTime recordedAt) {
        this.batchId = batchId;
        this.dataHash = dataHash;
        this.transactionHash = transactionHash;
        this.blockNumber = blockNumber;
        this.network = network;
        this.recordType = recordType;
        this.recordedAt = recordedAt != null ? recordedAt : LocalDateTime.now();
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
