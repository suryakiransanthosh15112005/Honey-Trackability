package com.honeychain.verification.controller;

import com.honeychain.common.dto.ApiResponse;
import com.honeychain.verification.dto.PublicVerificationResponse;
import com.honeychain.verification.dto.VerificationHistoryResponse;
import com.honeychain.verification.service.PublicVerificationService;
import com.honeychain.verification.service.VerificationHistoryService;
import com.honeychain.verification.util.VerificationFingerprintUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/verify")
@Tag(name = "Public Batch Verification & Anti-Counterfeit", description = "Public endpoints for QR code scan verification, history tracking, and anti-counterfeit risk detection (No login required)")
public class PublicVerificationController {

    private final PublicVerificationService publicVerificationService;
    private final VerificationHistoryService verificationHistoryService;
    private final VerificationFingerprintUtil verificationFingerprintUtil;

    public PublicVerificationController(PublicVerificationService publicVerificationService,
            VerificationHistoryService verificationHistoryService,
            VerificationFingerprintUtil verificationFingerprintUtil) {
        this.publicVerificationService = publicVerificationService;
        this.verificationHistoryService = verificationHistoryService;
        this.verificationFingerprintUtil = verificationFingerprintUtil;
    }

    @GetMapping("/{batchId}")
    @Operation(summary = "Verify Batch Authenticity (Public)", description = "Retrieves verified batch, beekeeper, purity, blockchain, and verification history summary")
    public ResponseEntity<ApiResponse<PublicVerificationResponse>> verifyBatch(@PathVariable String batchId) {
        PublicVerificationResponse response = publicVerificationService.verifyBatch(batchId);
        return ResponseEntity.ok(ApiResponse.success("Batch verification completed", response));
    }

    @PostMapping("/{batchId}/scan")
    @Operation(summary = "Record QR Scan & Verify (Public)", description = "Records a consumer QR scan event with privacy-preserving fingerprint and returns complete verification data with updated risk heuristics")
    public ResponseEntity<ApiResponse<PublicVerificationResponse>> scanAndVerifyBatch(
            @PathVariable String batchId,
            HttpServletRequest request) {
        String fingerprint = verificationFingerprintUtil.generateFingerprint(request);
        PublicVerificationResponse response = publicVerificationService.scanAndVerifyBatch(batchId, null, fingerprint);
        return ResponseEntity.ok(ApiResponse.success("Scan recorded and batch verified", response));
    }

    @GetMapping("/{batchId}/history")
    @Operation(summary = "Get Verification History (Public)", description = "Retrieves public scan count, anti-counterfeit risk level, last verified date, and recent verification timeline")
    public ResponseEntity<ApiResponse<VerificationHistoryResponse>> getVerificationHistory(
            @PathVariable String batchId) {
        VerificationHistoryResponse response = verificationHistoryService.getVerificationHistory(batchId);
        return ResponseEntity.ok(ApiResponse.success("Verification history retrieved", response));
    }
}
