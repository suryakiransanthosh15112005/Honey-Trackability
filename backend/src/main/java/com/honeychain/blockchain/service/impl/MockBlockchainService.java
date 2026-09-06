package com.honeychain.blockchain.service.impl;

import com.honeychain.batch.entity.HoneyBatch;
import com.honeychain.blockchain.dto.BlockchainRecordResponse;
import com.honeychain.blockchain.entity.BlockchainRecord;
import com.honeychain.blockchain.entity.BlockchainRecordType;
import com.honeychain.blockchain.exception.BlockchainException;
import com.honeychain.blockchain.mapper.BlockchainRecordMapper;
import com.honeychain.blockchain.repository.BlockchainRecordRepository;
import com.honeychain.blockchain.service.BlockchainService;
import com.honeychain.blockchain.util.BatchCanonicalDataBuilder;
import com.honeychain.blockchain.util.HashUtil;
import com.honeychain.blockchain.util.LabResultCanonicalDataBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "blockchain.mode", havingValue = "mock", matchIfMissing = true)
public class MockBlockchainService implements BlockchainService {

    private static final Logger logger = LoggerFactory.getLogger(MockBlockchainService.class);
    public static final String MOCK_NETWORK = "HONEYCHAIN-MOCKNET";
    private static final long INITIAL_BLOCK_BASE = 102400L;

    private final BlockchainRecordRepository blockchainRecordRepository;
    private final BlockchainRecordMapper blockchainRecordMapper;

    public MockBlockchainService(BlockchainRecordRepository blockchainRecordRepository,
                                 BlockchainRecordMapper blockchainRecordMapper) {
        this.blockchainRecordRepository = blockchainRecordRepository;
        this.blockchainRecordMapper = blockchainRecordMapper;
    }

    @Override
    @Transactional
    public BlockchainRecordResponse recordBatch(HoneyBatch batch) {
        if (batch == null || batch.getBatchId() == null) {
            throw new BlockchainException("Cannot record batch with null or missing batch ID");
        }

        // Check if already recorded to ensure idempotence
        Optional<BlockchainRecord> existing = blockchainRecordRepository.findByBatchIdAndRecordType(
                batch.getBatchId(), BlockchainRecordType.BATCH_CREATED);
        if (existing.isPresent()) {
            logger.info("Batch {} already recorded on blockchain (Block: {})",
                    batch.getBatchId(), existing.get().getBlockNumber());
            return blockchainRecordMapper.toResponse(existing.get());
        }

        // 1. Build canonical string
        String canonicalString = BatchCanonicalDataBuilder.buildCanonicalString(batch);

        // 2. Generate SHA-256 data hash
        String dataHash = HashUtil.generateSha256(canonicalString);

        // 3. Generate transaction hash (0x + SHA-256 of dataHash + random salt)
        String rawTxData = dataHash + ":" + UUID.randomUUID() + ":" + System.currentTimeMillis();
        String transactionHash = "0x" + HashUtil.generateSha256(rawTxData);

        // 4. Generate incrementing simulated block number
        long currentRecordCount = blockchainRecordRepository.count();
        long blockNumber = INITIAL_BLOCK_BASE + currentRecordCount + 1;

        // 5. Persist immutable record
        BlockchainRecord record = new BlockchainRecord(
                batch.getBatchId(),
                dataHash,
                transactionHash,
                blockNumber,
                MOCK_NETWORK,
                BlockchainRecordType.BATCH_CREATED,
                LocalDateTime.now()
        );

        BlockchainRecord saved = blockchainRecordRepository.save(record);
        logger.info("Recorded batch {} on {} at block {} with tx {}",
                batch.getBatchId(), MOCK_NETWORK, blockNumber, transactionHash);

        return blockchainRecordMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public BlockchainRecordResponse recordLabResult(String batchId, Integer purityScore, String result, LocalDateTime testedAt) {
        if (batchId == null || batchId.isBlank()) {
            throw new BlockchainException("Cannot record lab result: batchId is required");
        }

        // Check if already recorded to ensure idempotence
        Optional<BlockchainRecord> existing = blockchainRecordRepository.findByBatchIdAndRecordType(
                batchId, BlockchainRecordType.LAB_RESULT);
        if (existing.isPresent()) {
            logger.info("Lab result for batch {} already recorded on blockchain (Block: {})",
                    batchId, existing.get().getBlockNumber());
            return blockchainRecordMapper.toResponse(existing.get());
        }

        // 1. Build canonical lab result string
        String canonicalString = LabResultCanonicalDataBuilder.buildCanonicalString(batchId, purityScore, result, testedAt);

        // 2. Generate SHA-256 data hash
        String dataHash = HashUtil.generateSha256(canonicalString);

        // 3. Generate transaction hash
        String rawTxData = dataHash + ":LAB:" + UUID.randomUUID() + ":" + System.currentTimeMillis();
        String transactionHash = "0x" + HashUtil.generateSha256(rawTxData);

        // 4. Generate block number
        long currentRecordCount = blockchainRecordRepository.count();
        long blockNumber = INITIAL_BLOCK_BASE + currentRecordCount + 1;

        // 5. Persist immutable record
        BlockchainRecord record = new BlockchainRecord(
                batchId,
                dataHash,
                transactionHash,
                blockNumber,
                MOCK_NETWORK,
                BlockchainRecordType.LAB_RESULT,
                LocalDateTime.now()
        );

        BlockchainRecord saved = blockchainRecordRepository.save(record);
        logger.info("Recorded LAB_RESULT for batch {} ({}) on {} at block {} with tx {}",
                batchId, result, MOCK_NETWORK, blockNumber, transactionHash);

        return blockchainRecordMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BlockchainRecordResponse getBatchRecord(String batchId) {
        return getBatchRecord(batchId, BlockchainRecordType.BATCH_CREATED);
    }

    @Override
    @Transactional(readOnly = true)
    public BlockchainRecordResponse getBatchRecord(String batchId, BlockchainRecordType recordType) {
        return blockchainRecordRepository.findByBatchIdAndRecordType(batchId, recordType)
                .map(blockchainRecordMapper::toResponse)
                .orElseGet(() -> new BlockchainRecordResponse(false, batchId, null, null, null, null, null, null));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isBatchRecorded(String batchId) {
        return blockchainRecordRepository.existsByBatchIdAndRecordType(batchId, BlockchainRecordType.BATCH_CREATED);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isLabResultRecorded(String batchId) {
        return blockchainRecordRepository.existsByBatchIdAndRecordType(batchId, BlockchainRecordType.LAB_RESULT);
    }
}
