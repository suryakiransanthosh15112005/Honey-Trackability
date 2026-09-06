package com.honeychain.verification.service.impl;

import com.honeychain.verification.dto.VerificationRiskResponse;
import com.honeychain.verification.entity.VerificationRiskLevel;
import com.honeychain.verification.repository.VerificationHistoryRepository;
import com.honeychain.verification.service.VerificationRiskService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VerificationRiskServiceImpl implements VerificationRiskService {

    private final VerificationHistoryRepository verificationHistoryRepository;

    @Value("${verification.risk.watch-threshold:20}")
    private long watchThreshold;

    @Value("${verification.risk.high-risk-threshold:50}")
    private long highRiskThreshold;

    @Value("${verification.risk.short-window-hours:24}")
    private int shortWindowHours;

    @Value("${verification.risk.short-window-burst-threshold:15}")
    private long burstThreshold;

    public VerificationRiskServiceImpl(VerificationHistoryRepository verificationHistoryRepository) {
        this.verificationHistoryRepository = verificationHistoryRepository;
    }

    @Override
    public VerificationRiskResponse calculateRisk(String batchId, long totalScans) {
        LocalDateTime windowStart = LocalDateTime.now().minusHours(shortWindowHours);
        long recentScans = verificationHistoryRepository.countByBatchIdAndScannedAtAfter(batchId, windowStart);

        if (totalScans >= highRiskThreshold || recentScans >= (burstThreshold * 2)) {
            return new VerificationRiskResponse(
                    VerificationRiskLevel.HIGH_RISK,
                    "Unusual verification activity detected. Please verify the product source before purchase."
            );
        } else if (totalScans >= watchThreshold || recentScans >= burstThreshold) {
            return new VerificationRiskResponse(
                    VerificationRiskLevel.WATCH,
                    "This batch has higher-than-usual verification activity."
            );
        } else {
            return new VerificationRiskResponse(
                    VerificationRiskLevel.NORMAL,
                    "Verification activity appears normal."
            );
        }
    }
}
