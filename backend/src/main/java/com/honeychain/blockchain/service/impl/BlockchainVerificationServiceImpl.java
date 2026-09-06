package com.honeychain.blockchain.service.impl;

import com.honeychain.batch.entity.HoneyBatch;
import com.honeychain.batch.repository.HoneyBatchRepository;
import com.honeychain.beekeeper.entity.BeekeeperProfile;
import com.honeychain.beekeeper.repository.BeekeeperProfileRepository;
import com.honeychain.blockchain.dto.BlockchainVerificationResponse;
import com.honeychain.blockchain.entity.BlockchainRecord;
import com.honeychain.blockchain.entity.BlockchainRecordType;
import com.honeychain.blockchain.repository.BlockchainRecordRepository;
import com.honeychain.blockchain.service.BlockchainVerificationService;
import com.honeychain.blockchain.util.BatchCanonicalDataBuilder;
import com.honeychain.blockchain.util.HashUtil;
import com.honeychain.common.exception.BadRequestException;
import com.honeychain.common.exception.ResourceNotFoundException;
import com.honeychain.user.entity.User;
import com.honeychain.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class BlockchainVerificationServiceImpl implements BlockchainVerificationService {

    private static final Logger logger = LoggerFactory.getLogger(BlockchainVerificationServiceImpl.class);

    private final BlockchainRecordRepository blockchainRecordRepository;
    private final HoneyBatchRepository honeyBatchRepository;
    private final BeekeeperProfileRepository beekeeperProfileRepository;
    private final UserService userService;

    public BlockchainVerificationServiceImpl(BlockchainRecordRepository blockchainRecordRepository,
            HoneyBatchRepository honeyBatchRepository,
            BeekeeperProfileRepository beekeeperProfileRepository,
            UserService userService) {
        this.blockchainRecordRepository = blockchainRecordRepository;
        this.honeyBatchRepository = honeyBatchRepository;
        this.beekeeperProfileRepository = beekeeperProfileRepository;
        this.userService = userService;
    }

    @Override
    @Transactional(readOnly = true)
    public BlockchainVerificationResponse verifyBatch(String batchId, HoneyBatch currentBatch) {
        if (currentBatch == null) {
            throw new BadRequestException("Current batch data is required for verification");
        }

        Optional<BlockchainRecord> recordOpt = blockchainRecordRepository.findByBatchIdAndRecordType(
                batchId, BlockchainRecordType.BATCH_CREATED);

        if (recordOpt.isEmpty()) {
            logger.warn("Verification requested for batch {} but no blockchain record exists", batchId);
            return BlockchainVerificationResponse.notFound(batchId);
        }

        BlockchainRecord record = recordOpt.get();

        // 1. Rebuild canonical string from current state of the batch
        String currentCanonical = BatchCanonicalDataBuilder.buildCanonicalString(currentBatch);

        // 2. Generate current SHA-256 hash
        String calculatedHash = HashUtil.generateSha256(currentCanonical);
        String storedHash = record.getDataHash();

        // 3. Compare hashes
        boolean isMatch = calculatedHash.equalsIgnoreCase(storedHash);

        if (isMatch) {
            logger.info("Blockchain verification SUCCESS for batch {} (Hash: {})", batchId, storedHash);
            return BlockchainVerificationResponse.success(
                    batchId,
                    storedHash,
                    calculatedHash,
                    record.getTransactionHash(),
                    record.getBlockNumber(),
                    record.getNetwork());
        } else {
            logger.warn("Blockchain verification TAMPER DETECTED for batch {}! Stored: {}, Calculated: {}",
                    batchId, storedHash, calculatedHash);
            return BlockchainVerificationResponse.tampered(
                    batchId,
                    storedHash,
                    calculatedHash,
                    record.getTransactionHash(),
                    record.getBlockNumber(),
                    record.getNetwork());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BlockchainVerificationResponse verifyBatchForBeekeeper(String phoneNumber, String batchId) {
        User user = userService.findEntityByPhoneNumber(phoneNumber);
        BeekeeperProfile profile = beekeeperProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BadRequestException("Beekeeper profile not found"));

        HoneyBatch batch = honeyBatchRepository.findByBatchIdAndBeekeeperProfileId(batchId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("HoneyBatch", "batchId", batchId));

        return verifyBatch(batchId, batch);
    }
}
