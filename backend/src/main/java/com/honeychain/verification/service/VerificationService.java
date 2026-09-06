package com.honeychain.verification.service;

import java.util.Map;

public interface VerificationService {

    Map<String, Object> verifyHoneyBatch(String batchId);
}
