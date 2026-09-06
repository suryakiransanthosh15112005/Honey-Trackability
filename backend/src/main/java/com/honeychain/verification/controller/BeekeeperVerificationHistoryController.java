package com.honeychain.verification.controller;

import com.honeychain.common.dto.ApiResponse;
import com.honeychain.verification.dto.VerificationHistoryResponse;
import com.honeychain.verification.service.VerificationHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/beekeepers/batches/{batchId}")
@Tag(name = "Beekeeper Verification Analytics", description = "Beekeeper endpoints for monitoring consumer verification activity and anti-counterfeit signals")
@SecurityRequirement(name = "Bearer Authentication")
public class BeekeeperVerificationHistoryController {

    private final VerificationHistoryService verificationHistoryService;

    public BeekeeperVerificationHistoryController(VerificationHistoryService verificationHistoryService) {
        this.verificationHistoryService = verificationHistoryService;
    }

    @GetMapping("/verification-history")
    @Operation(summary = "Get Batch Verification Activity (Beekeeper)", description = "Retrieves scan statistics and anti-counterfeit risk indicators for an owned honey batch")
    public ResponseEntity<ApiResponse<VerificationHistoryResponse>> getBeekeeperBatchHistory(
            Authentication authentication,
            @PathVariable String batchId) {
        VerificationHistoryResponse response = verificationHistoryService.getVerificationHistoryForBeekeeper(
                authentication.getName(), batchId);
        return ResponseEntity.ok(ApiResponse.success("Batch verification activity retrieved", response));
    }
}
