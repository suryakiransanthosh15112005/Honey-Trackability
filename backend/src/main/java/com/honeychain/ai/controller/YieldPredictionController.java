package com.honeychain.ai.controller;

import com.honeychain.ai.dto.YieldPredictionResponse;
import com.honeychain.ai.service.YieldPredictionService;
import com.honeychain.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/beekeepers/hives")
@Tag(name = "AI Yield Prediction", description = "AI-assisted hive harvest date & yield range prediction endpoints")
@PreAuthorize("hasRole('BEEKEEPER')")
public class YieldPredictionController {

    private final YieldPredictionService yieldPredictionService;

    public YieldPredictionController(YieldPredictionService yieldPredictionService) {
        this.yieldPredictionService = yieldPredictionService;
    }

    @GetMapping("/{hiveId}/yield-prediction")
    @Operation(summary = "Get AI yield prediction for a specific hive")
    public ResponseEntity<ApiResponse<YieldPredictionResponse>> getYieldPrediction(
            @PathVariable Long hiveId,
            Authentication authentication) {
        String phone = authentication.getName();
        YieldPredictionResponse prediction = yieldPredictionService.getPredictionForHive(hiveId, phone);
        return ResponseEntity.ok(ApiResponse.success("AI yield prediction retrieved successfully", prediction));
    }

    @GetMapping("/yield-predictions")
    @Operation(summary = "Get AI yield predictions for all owned hives")
    public ResponseEntity<ApiResponse<List<YieldPredictionResponse>>> getAllYieldPredictions(
            Authentication authentication) {
        String phone = authentication.getName();
        List<YieldPredictionResponse> predictions = yieldPredictionService.getAllPredictionsForBeekeeper(phone);
        return ResponseEntity.ok(ApiResponse.success("All AI yield predictions retrieved successfully", predictions));
    }

    @PostMapping("/{hiveId}/yield-prediction/refresh")
    @Operation(summary = "Recalculate & refresh AI yield prediction for a specific hive")
    public ResponseEntity<ApiResponse<YieldPredictionResponse>> refreshYieldPrediction(
            @PathVariable Long hiveId,
            Authentication authentication) {
        String phone = authentication.getName();
        YieldPredictionResponse prediction = yieldPredictionService.refreshPredictionForHive(hiveId, phone);
        return ResponseEntity.ok(ApiResponse.success("AI yield prediction refreshed successfully", prediction));
    }
}
