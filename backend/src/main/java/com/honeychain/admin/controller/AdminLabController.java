package com.honeychain.admin.controller;

import com.honeychain.admin.dto.AdminLabSummaryResponse;
import com.honeychain.admin.service.AdminLabService;
import com.honeychain.common.dto.ApiResponse;
import com.honeychain.common.dto.PageResponse;
import com.honeychain.lab.entity.LabTestResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/lab-tests")
@Tag(name = "Admin Lab Monitoring", description = "Laboratory testing records and quality assessments")
public class AdminLabController {

    private final AdminLabService adminLabService;

    public AdminLabController(AdminLabService adminLabService) {
        this.adminLabService = adminLabService;
    }

    @GetMapping
    @Operation(summary = "List Lab Tests", description = "Paginated lab tests with result filter (PURE, UNDER_REVIEW, FAILED)")
    public ResponseEntity<ApiResponse<PageResponse<AdminLabSummaryResponse>>> getLabTests(
            @RequestParam(required = false) LabTestResult result,
            @PageableDefault(size = 20) Pageable pageable) {
        PageResponse<AdminLabSummaryResponse> response = adminLabService.getLabTests(result, pageable);
        return ResponseEntity.ok(ApiResponse.success("Lab tests retrieved successfully", response));
    }
}
