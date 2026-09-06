package com.honeychain.blockchain.service;

import com.honeychain.batch.entity.HoneyBatch;
import com.honeychain.blockchain.dto.BlockchainRecordResponse;
import com.honeychain.blockchain.entity.BlockchainRecordType;

import java.time.LocalDateTime;

public interface BlockchainService {

    BlockchainRecordResponse recordBatch(HoneyBatch batch);

    BlockchainRecordResponse recordLabResult(String batchId, Integer purityScore, String result, LocalDateTime testedAt);

    BlockchainRecordResponse getBatchRecord(String batchId);

    BlockchainRecordResponse getBatchRecord(String batchId, BlockchainRecordType recordType);

    boolean isBatchRecorded(String batchId);

    boolean isLabResultRecorded(String batchId);
}
