package com.honeychain.verification.service;

import com.honeychain.verification.dto.PublicVerificationResponse;
import com.honeychain.verification.dto.VerificationHistoryResponse;
import com.honeychain.verification.entity.VerificationHistory;
import com.honeychain.verification.entity.VerificationResult;

public interface VerificationHistoryService {

    VerificationHistory recordVerificationEvent(String batchId, Long customerId, String fingerprint, VerificationResult result);

    VerificationHistoryResponse getVerificationHistory(String batchId);

    VerificationHistoryResponse getVerificationHistoryForBeekeeper(String phoneNumber, String batchId);

    PublicVerificationResponse.VerificationHistorySummary getSummary(String batchId);
}
