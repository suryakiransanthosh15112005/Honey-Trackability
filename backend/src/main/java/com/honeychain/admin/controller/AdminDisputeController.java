package com.honeychain.admin.controller;

import com.honeychain.admin.dto.DisputeResponse;
import com.honeychain.admin.dto.DisputeStatusUpdateRequest;
import com.honeychain.admin.entity.DisputeStatus;
import com.honeychain.admin.service.AdminDisputeService;
import com.honeychain.common.dto.ApiResponse;
import com.honeychain.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/disputes")
@Tag(name = "Admin Dispute Management", description = "Customer authenticity dispute investigation and resolution workflows")
public class AdminDisputeController {

    private final AdminDisputeService adminDisputeService;

    public AdminDisputeController(AdminDisputeService adminDisputeService) {
        this.adminDisputeService = adminDisputeService;
    }

    @GetMapping
    @Operation(summary = "List Disputes", description = "Paginated list of disputes with optional status filter")
    public ResponseEntity<ApiResponse<PageResponse<DisputeResponse>>> getDisputes(
            @RequestParam(required = false) DisputeStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        PageResponse<DisputeResponse> response = adminDisputeService.getDisputes(status, pageable);
        return ResponseEntity.ok(ApiResponse.success("Disputes retrieved successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Dispute Details", description = "Retrieve single dispute record")
    public ResponseEntity<ApiResponse<DisputeResponse>> getDisputeDetails(@PathVariable Long id) {
        DisputeResponse response = adminDisputeService.getDisputeDetails(id);
        return ResponseEntity.ok(ApiResponse.success("Dispute details retrieved successfully", response));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update Dispute Status", description = "Transition dispute state to INVESTIGATING, RESOLVED, or REJECTED")
    public ResponseEntity<ApiResponse<DisputeResponse>> updateDisputeStatus(
            @PathVariable Long id,
            @Valid @RequestBody DisputeStatusUpdateRequest request) {
        DisputeResponse response = adminDisputeService.updateDisputeStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Dispute status updated successfully", response));
    }
}
