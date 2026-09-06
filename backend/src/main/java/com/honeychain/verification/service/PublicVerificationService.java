package com.honeychain.verification.service;

import com.honeychain.verification.dto.PublicVerificationResponse;

public interface PublicVerificationService {

    PublicVerificationResponse verifyBatch(String batchId);

    PublicVerificationResponse scanAndVerifyBatch(String batchId, Long customerId, String fingerprint);
}
