package com.honeychain.verification.service.impl;

import com.honeychain.batch.repository.HoneyBatchRepository;
import com.honeychain.beekeeper.entity.BeekeeperProfile;
import com.honeychain.beekeeper.repository.BeekeeperProfileRepository;
import com.honeychain.common.exception.BadRequestException;
import com.honeychain.common.exception.ResourceNotFoundException;
import com.honeychain.user.entity.User;
import com.honeychain.user.service.UserService;
import com.honeychain.verification.dto.PublicVerificationResponse;
import com.honeychain.verification.dto.VerificationHistoryResponse;
import com.honeychain.verification.dto.VerificationRiskResponse;
import com.honeychain.verification.entity.VerificationHistory;
import com.honeychain.verification.entity.VerificationResult;
import com.honeychain.verification.mapper.VerificationHistoryMapper;
import com.honeychain.verification.repository.VerificationHistoryRepository;
import com.honeychain.verification.service.VerificationHistoryService;
import com.honeychain.verification.service.VerificationRiskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VerificationHistoryServiceImpl implements VerificationHistoryService {

    private static final Logger logger = LoggerFactory.getLogger(VerificationHistoryServiceImpl.class);

    private final VerificationHistoryRepository verificationHistoryRepository;
    private final VerificationRiskService verificationRiskService;
    private final HoneyBatchRepository honeyBatchRepository;
    private final BeekeeperProfileRepository beekeeperProfileRepository;
    private final UserService userService;
    private final VerificationHistoryMapper verificationHistoryMapper;

    @Value("${verification.debounce-seconds:5}")
    private long debounceSeconds;

    public VerificationHistoryServiceImpl(VerificationHistoryRepository verificationHistoryRepository,
            VerificationRiskService verificationRiskService,
            HoneyBatchRepository honeyBatchRepository,
            BeekeeperProfileRepository beekeeperProfileRepository,
            UserService userService,
            VerificationHistoryMapper verificationHistoryMapper) {
        this.verificationHistoryRepository = verificationHistoryRepository;
        this.verificationRiskService = verificationRiskService;
        this.honeyBatchRepository = honeyBatchRepository;
        this.beekeeperProfileRepository = beekeeperProfileRepository;
        this.userService = userService;
        this.verificationHistoryMapper = verificationHistoryMapper;
    }

    @Override
    @Transactional
    public VerificationHistory recordVerificationEvent(String batchId, Long customerId, String fingerprint,
            VerificationResult result) {
        if (batchId == null || batchId.isBlank())
            return null;

        LocalDateTime now = LocalDateTime.now();

        // 1. Idempotency / Debounce Protection (Avoid rapid mobile refresh duplication)
        if (fingerprint != null && !fingerprint.isBlank()) {
            Optional<VerificationHistory> recentScanOpt = verificationHistoryRepository
                    .findTop1ByBatchIdAndVerificationFingerprintOrderByScannedAtDesc(batchId, fingerprint);

            if (recentScanOpt.isPresent()) {
                VerificationHistory recentScan = recentScanOpt.get();
                if (recentScan.getScannedAt().isAfter(now.minusSeconds(debounceSeconds))) {
                    logger.debug("Debounced duplicate scan for batch {} with fingerprint {}", batchId, fingerprint);
                    return recentScan;
                }
            }
        }

        // 2. Calculate current total count and risk level
        long currentTotal = verificationHistoryRepository.countByBatchId(batchId) + 1;
        VerificationRiskResponse risk = verificationRiskService.calculateRisk(batchId, currentTotal);

        // 3. Persist verification history event
        VerificationHistory history = new VerificationHistory(
                batchId,
                customerId,
                fingerprint != null ? fingerprint : "anonymous",
                result != null ? result : VerificationResult.VERIFIED,
                risk.getRiskLevel(),
                now);

        VerificationHistory saved = verificationHistoryRepository.save(history);
        logger.info("Recorded verification scan for batch {}: Result={}, Risk={}, TotalScans={}",
                batchId, result, risk.getRiskLevel(), currentTotal);

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public VerificationHistoryResponse getVerificationHistory(String batchId) {
        long totalCount = verificationHistoryRepository.countByBatchId(batchId);
        VerificationRiskResponse risk = verificationRiskService.calculateRisk(batchId, totalCount);

        LocalDateTime lastVerifiedAt = verificationHistoryRepository.findTop1ByBatchIdOrderByScannedAtDesc(batchId)
                .map(VerificationHistory::getScannedAt)
                .orElse(null);

        List<VerificationHistory> recent = verificationHistoryRepository
                .findTop10ByBatchIdOrderByScannedAtDesc(batchId);

        return verificationHistoryMapper.toHistoryResponse(totalCount, risk, lastVerifiedAt, recent);
    }

    @Override
    @Transactional(readOnly = true)
    public VerificationHistoryResponse getVerificationHistoryForBeekeeper(String phoneNumber, String batchId) {
        User user = userService.findEntityByPhoneNumber(phoneNumber);
        BeekeeperProfile profile = beekeeperProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BadRequestException("Beekeeper profile not found"));

        honeyBatchRepository.findByBatchIdAndBeekeeperProfileId(batchId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("HoneyBatch", "batchId", batchId));

        return getVerificationHistory(batchId);
    }

    @Override
    @Transactional(readOnly = true)
    public PublicVerificationResponse.VerificationHistorySummary getSummary(String batchId) {
        long totalCount = verificationHistoryRepository.countByBatchId(batchId);
        VerificationRiskResponse risk = verificationRiskService.calculateRisk(batchId, totalCount);

        LocalDateTime lastVerifiedAt = verificationHistoryRepository.findTop1ByBatchIdOrderByScannedAtDesc(batchId)
                .map(VerificationHistory::getScannedAt)
                .orElse(null);

        return new PublicVerificationResponse.VerificationHistorySummary(
                totalCount,
                risk.getRiskLevel(),
                risk.getMessage(),
                lastVerifiedAt);
    }
}
