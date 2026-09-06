package com.honeychain.blockchain.controller;

import com.honeychain.blockchain.dto.BlockchainRecordResponse;
import com.honeychain.blockchain.dto.BlockchainVerificationResponse;
import com.honeychain.blockchain.service.BlockchainService;
import com.honeychain.blockchain.service.BlockchainVerificationService;
import com.honeychain.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/beekeepers/batches/{batchId}/blockchain")
@Tag(name = "Blockchain Verification", description = "Endpoints for immutable batch hashing and verification")
@SecurityRequirement(name = "Bearer Authentication")
public class BlockchainController {

    private final BlockchainService blockchainService;
    private final BlockchainVerificationService blockchainVerificationService;

    public BlockchainController(BlockchainService blockchainService,
                                BlockchainVerificationService blockchainVerificationService) {
        this.blockchainService = blockchainService;
        this.blockchainVerificationService = blockchainVerificationService;
    }

    @GetMapping
    @Operation(summary = "Get Blockchain Record", description = "Retrieve the immutable blockchain record for a specific batch")
    public ResponseEntity<ApiResponse<BlockchainRecordResponse>> getBlockchainRecord(
            Authentication authentication,
            @PathVariable String batchId) {
        // Verification service validates ownership first
        blockchainVerificationService.verifyBatchForBeekeeper(authentication.getName(), batchId);
        BlockchainRecordResponse record = blockchainService.getBatchRecord(batchId);
        return ResponseEntity.ok(ApiResponse.success("Blockchain record retrieved", record));
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify Batch on Blockchain", description = "Recalculate batch SHA-256 hash and verify against the immutable blockchain ledger")
    public ResponseEntity<ApiResponse<BlockchainVerificationResponse>> verifyBatch(
            Authentication authentication,
            @PathVariable String batchId) {
        BlockchainVerificationResponse response = blockchainVerificationService.verifyBatchForBeekeeper(
                authentication.getName(), batchId);
        return ResponseEntity.ok(ApiResponse.success(
                response.isVerified() ? "Batch verified successfully" : "Batch verification failed - data modified",
                response
        ));
    }
}
