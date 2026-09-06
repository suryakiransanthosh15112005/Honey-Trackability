package com.honeychain.admin.controller;

import com.honeychain.admin.dto.AdminBatchDetailsResponse;
import com.honeychain.admin.dto.AdminBatchResponse;
import com.honeychain.admin.service.AdminBatchService;
import com.honeychain.batch.entity.BatchStatus;
import com.honeychain.common.dto.ApiResponse;
import com.honeychain.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/batches")
@Tag(name = "Admin Batch Monitoring", description = "Honey batch monitoring, verification activity, and blockchain ledger inspection")
public class AdminBatchController {

    private final AdminBatchService adminBatchService;

    public AdminBatchController(AdminBatchService adminBatchService) {
        this.adminBatchService = adminBatchService;
    }

    @GetMapping
    @Operation(summary = "List and Filter Batches", description = "Paginated list of honey batches with status and batch ID search filters")
    public ResponseEntity<ApiResponse<PageResponse<AdminBatchResponse>>> getBatches(
            @RequestParam(required = false) BatchStatus status,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {
        PageResponse<AdminBatchResponse> response = adminBatchService.getBatches(status, search, pageable);
        return ResponseEntity.ok(ApiResponse.success("Batches retrieved successfully", response));
    }

    @GetMapping("/{batchId}")
    @Operation(summary = "Get Batch Details", description = "Detailed view of a batch including lab test results, blockchain transaction, QR code, and scan logs")
    public ResponseEntity<ApiResponse<AdminBatchDetailsResponse>> getBatchDetails(@PathVariable String batchId) {
        AdminBatchDetailsResponse response = adminBatchService.getBatchDetails(batchId);
        return ResponseEntity.ok(ApiResponse.success("Batch details retrieved successfully", response));
    }
}
