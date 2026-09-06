package com.honeychain.admin.controller;

import com.honeychain.admin.dto.ProductionTrendResponse;
import com.honeychain.admin.dto.PurityAnalyticsResponse;
import com.honeychain.admin.dto.RegionalAnalyticsResponse;
import com.honeychain.admin.dto.SalesAnalyticsResponse;
import com.honeychain.admin.dto.VerificationRiskAnalyticsResponse;
import com.honeychain.admin.service.AdminAnalyticsService;
import com.honeychain.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/analytics")
@Tag(name = "Admin Analytics", description = "Regional analytics, production trends, purity statistics, sales, and verification risk indicators")
public class AdminAnalyticsController {

    private final AdminAnalyticsService adminAnalyticsService;

    public AdminAnalyticsController(AdminAnalyticsService adminAnalyticsService) {
        this.adminAnalyticsService = adminAnalyticsService;
    }

    @GetMapping("/purity")
    @Operation(summary = "Purity Analytics", description = "Overall pass rate, test outcomes, and average purity scores")
    public ResponseEntity<ApiResponse<PurityAnalyticsResponse>> getPurityAnalytics() {
        PurityAnalyticsResponse response = adminAnalyticsService.getPurityAnalytics();
        return ResponseEntity.ok(ApiResponse.success("Purity analytics retrieved successfully", response));
    }

    @GetMapping("/regions")
    @Operation(summary = "Regional Analytics", description = "Regional distribution of beekeepers, hives, batches, honey produced, and average purity")
    public ResponseEntity<ApiResponse<List<RegionalAnalyticsResponse>>> getRegionalAnalytics() {
        List<RegionalAnalyticsResponse> response = adminAnalyticsService.getRegionalAnalytics();
        return ResponseEntity.ok(ApiResponse.success("Regional analytics retrieved successfully", response));
    }

    @GetMapping("/production")
    @Operation(summary = "Production Trend", description = "Monthly honey harvest volume and batch counts")
    public ResponseEntity<ApiResponse<List<ProductionTrendResponse>>> getProductionTrend() {
        List<ProductionTrendResponse> response = adminAnalyticsService.getProductionTrend();
        return ResponseEntity.ok(ApiResponse.success("Production trend retrieved successfully", response));
    }

    @GetMapping("/sales")
    @Operation(summary = "Sales & Orders Analytics", description = "Monthly sales volume, revenue, completed and cancelled order counts")
    public ResponseEntity<ApiResponse<List<SalesAnalyticsResponse>>> getSalesAnalytics() {
        List<SalesAnalyticsResponse> response = adminAnalyticsService.getSalesAnalytics();
        return ResponseEntity.ok(ApiResponse.success("Sales analytics retrieved successfully", response));
    }

    @GetMapping("/verification-risk")
    @Operation(summary = "Verification Risk Analytics", description = "Anti-counterfeit risk distribution and high-risk flagged batches")
    public ResponseEntity<ApiResponse<VerificationRiskAnalyticsResponse>> getVerificationRiskAnalytics() {
        VerificationRiskAnalyticsResponse response = adminAnalyticsService.getVerificationRiskAnalytics();
        return ResponseEntity.ok(ApiResponse.success("Verification risk analytics retrieved successfully", response));
    }
}
