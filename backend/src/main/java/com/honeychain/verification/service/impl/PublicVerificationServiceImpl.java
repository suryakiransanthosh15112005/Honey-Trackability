package com.honeychain.verification.service.impl;

import com.honeychain.batch.entity.BatchStatus;
import com.honeychain.batch.entity.HoneyBatch;
import com.honeychain.batch.repository.HoneyBatchRepository;
import com.honeychain.beekeeper.entity.BeekeeperProfile;
import com.honeychain.beekeeper.repository.BeekeeperProfileRepository;
import com.honeychain.blockchain.entity.BlockchainRecord;
import com.honeychain.blockchain.entity.BlockchainRecordType;
import com.honeychain.blockchain.repository.BlockchainRecordRepository;
import com.honeychain.blockchain.util.BatchCanonicalDataBuilder;
import com.honeychain.blockchain.util.HashUtil;
import com.honeychain.blockchain.util.LabResultCanonicalDataBuilder;
import com.honeychain.hive.entity.Hive;
import com.honeychain.hive.repository.HiveRepository;
import com.honeychain.lab.entity.LabTest;
import com.honeychain.lab.entity.LabTestResult;
import com.honeychain.lab.repository.LabTestRepository;
import com.honeychain.qr.entity.QrCode;
import com.honeychain.qr.repository.QrCodeRepository;
import com.honeychain.verification.dto.PublicVerificationResponse;
import com.honeychain.verification.entity.VerificationResult;
import com.honeychain.verification.mapper.PublicVerificationMapper;
import com.honeychain.verification.service.PublicVerificationService;
import com.honeychain.verification.service.VerificationHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PublicVerificationServiceImpl implements PublicVerificationService {

    private static final Logger logger = LoggerFactory.getLogger(PublicVerificationServiceImpl.class);

    private final HoneyBatchRepository honeyBatchRepository;
    private final BeekeeperProfileRepository beekeeperProfileRepository;
    private final HiveRepository hiveRepository;
    private final LabTestRepository labTestRepository;
    private final BlockchainRecordRepository blockchainRecordRepository;
    private final QrCodeRepository qrCodeRepository;
    private final VerificationHistoryService verificationHistoryService;
    private final PublicVerificationMapper publicVerificationMapper;

    public PublicVerificationServiceImpl(HoneyBatchRepository honeyBatchRepository,
                                         BeekeeperProfileRepository beekeeperProfileRepository,
                                         HiveRepository hiveRepository,
                                         LabTestRepository labTestRepository,
                                         BlockchainRecordRepository blockchainRecordRepository,
                                         QrCodeRepository qrCodeRepository,
                                         VerificationHistoryService verificationHistoryService,
                                         PublicVerificationMapper publicVerificationMapper) {
        this.honeyBatchRepository = honeyBatchRepository;
        this.beekeeperProfileRepository = beekeeperProfileRepository;
        this.hiveRepository = hiveRepository;
        this.labTestRepository = labTestRepository;
        this.blockchainRecordRepository = blockchainRecordRepository;
        this.qrCodeRepository = qrCodeRepository;
        this.verificationHistoryService = verificationHistoryService;
        this.publicVerificationMapper = publicVerificationMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PublicVerificationResponse verifyBatch(String batchId) {
        return processVerification(batchId, null, null, false);
    }

    @Override
    @Transactional
    public PublicVerificationResponse scanAndVerifyBatch(String batchId, Long customerId, String fingerprint) {
        return processVerification(batchId, customerId, fingerprint, true);
    }

    private PublicVerificationResponse processVerification(String batchId, Long customerId, String fingerprint, boolean recordScan) {
        if (batchId == null || batchId.isBlank()) {
            return publicVerificationMapper.toNotFoundResponse("UNKNOWN");
        }

        String cleanBatchId = batchId.trim();

        // 1. Batch lookup
        Optional<HoneyBatch> batchOpt = honeyBatchRepository.findByBatchId(cleanBatchId);
        if (batchOpt.isEmpty()) {
            logger.warn("Public verification requested for non-existent batch: {}", cleanBatchId);
            if (recordScan) {
                verificationHistoryService.recordVerificationEvent(cleanBatchId, customerId, fingerprint, VerificationResult.NOT_FOUND);
            }
            return publicVerificationMapper.toNotFoundResponse(cleanBatchId);
        }

        HoneyBatch batch = batchOpt.get();

        // 2. Fetch associated entities
        BeekeeperProfile profile = beekeeperProfileRepository.findById(batch.getBeekeeperProfileId()).orElse(null);
        Hive hive = hiveRepository.findById(batch.getHiveId()).orElse(null);
        Optional<LabTest> labTestOpt = labTestRepository.findByBatchId(batch.getBatchId());
        Optional<BlockchainRecord> batchBcOpt = blockchainRecordRepository.findByBatchIdAndRecordType(
                batch.getBatchId(), BlockchainRecordType.BATCH_CREATED);
        Optional<BlockchainRecord> labBcOpt = blockchainRecordRepository.findByBatchIdAndRecordType(
                batch.getBatchId(), BlockchainRecordType.LAB_RESULT);
        Optional<QrCode> qrCodeOpt = qrCodeRepository.findByBatchId(batch.getBatchId());

        // 3. Verify Batch Blockchain Integrity
        boolean batchIntegrityValid = false;
        if (batchBcOpt.isPresent()) {
            String currentBatchCanonical = BatchCanonicalDataBuilder.buildCanonicalString(batch);
            String currentBatchHash = HashUtil.generateSha256(currentBatchCanonical);
            batchIntegrityValid = currentBatchHash.equalsIgnoreCase(batchBcOpt.get().getDataHash());
        }

        // 4. Verify Lab Blockchain Integrity
        boolean labIntegrityValid = false;
        if (labTestOpt.isPresent() && labBcOpt.isPresent()) {
            LabTest lt = labTestOpt.get();
            String currentLabCanonical = LabResultCanonicalDataBuilder.buildCanonicalString(
                    batch.getBatchId(), lt.getPurityScore(), lt.getResult().name(), lt.getTestedAt());
            String currentLabHash = HashUtil.generateSha256(currentLabCanonical);
            labIntegrityValid = currentLabHash.equalsIgnoreCase(labBcOpt.get().getDataHash());
        }

        // 5. Determine Overall Trust & Verification Status
        String verificationStatus;
        String message;
        VerificationResult historyResult;
        boolean overallIntegrity = batchIntegrityValid && (labTestOpt.isEmpty() || labIntegrityValid);

        if (!overallIntegrity) {
            verificationStatus = "FAILED";
            historyResult = VerificationResult.FAILED;
            message = "Blockchain integrity check failed. Data inconsistency or tampering detected.";
        } else if (labTestOpt.isPresent()) {
            LabTest lt = labTestOpt.get();
            if (lt.getResult() == LabTestResult.PURE &&
                    (batch.getStatus() == BatchStatus.PURE || batch.getStatus() == BatchStatus.QR_GENERATED)) {
                verificationStatus = "GENUINE";
                historyResult = VerificationResult.VERIFIED;
                message = "Authentic honey batch verified on HoneyChain blockchain.";
            } else if (lt.getResult() == LabTestResult.UNDER_REVIEW) {
                verificationStatus = "UNDER_REVIEW";
                historyResult = VerificationResult.UNDER_REVIEW;
                message = "This batch is currently under laboratory re-examination.";
            } else {
                verificationStatus = "FAILED";
                historyResult = VerificationResult.FAILED;
                message = "This batch did not meet purity requirements.";
            }
        } else {
            verificationStatus = "NOT_AVAILABLE";
            historyResult = VerificationResult.NOT_AVAILABLE;
            message = "Laboratory analysis has not been completed for this batch.";
        }

        // 6. Record Verification Event if requested
        if (recordScan) {
            verificationHistoryService.recordVerificationEvent(cleanBatchId, customerId, fingerprint, historyResult);
        }

        // 7. Get Verification History & Anti-Counterfeit Summary
        PublicVerificationResponse.VerificationHistorySummary historySummary =
                verificationHistoryService.getSummary(cleanBatchId);

        return publicVerificationMapper.toResponse(
                batch,
                profile,
                hive,
                labTestOpt.orElse(null),
                batchBcOpt.orElse(null),
                labBcOpt.orElse(null),
                qrCodeOpt.orElse(null),
                overallIntegrity,
                verificationStatus,
                message,
                historySummary
        );
    }
}
