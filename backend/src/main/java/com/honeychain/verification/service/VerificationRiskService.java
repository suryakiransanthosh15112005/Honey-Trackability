package com.honeychain.verification.service;

import com.honeychain.verification.dto.VerificationRiskResponse;

public interface VerificationRiskService {

    VerificationRiskResponse calculateRisk(String batchId, long totalScans);
}
