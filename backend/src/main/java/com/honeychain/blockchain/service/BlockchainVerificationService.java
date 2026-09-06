package com.honeychain.blockchain.service;

import com.honeychain.batch.entity.HoneyBatch;
import com.honeychain.blockchain.dto.BlockchainVerificationResponse;

public interface BlockchainVerificationService {

    BlockchainVerificationResponse verifyBatch(String batchId, HoneyBatch currentBatch);

    BlockchainVerificationResponse verifyBatchForBeekeeper(String phoneNumber, String batchId);
}
