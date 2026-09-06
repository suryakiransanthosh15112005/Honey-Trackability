package com.honeychain.admin.controller;

import com.honeychain.admin.dto.AdminBeekeeperResponse;
import com.honeychain.admin.dto.AdminBeekeeperStatusUpdateRequest;
import com.honeychain.admin.service.AdminBeekeeperService;
import com.honeychain.beekeeper.entity.BeekeeperVerificationStatus;
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
@RequestMapping("/api/admin/beekeepers")
@Tag(name = "Admin Beekeeper Management", description = "Beekeeper verification, status approval, and profile audit endpoints")
public class AdminBeekeeperController {

    private final AdminBeekeeperService adminBeekeeperService;

    public AdminBeekeeperController(AdminBeekeeperService adminBeekeeperService) {
        this.adminBeekeeperService = adminBeekeeperService;
    }

    @GetMapping
    @Operation(summary = "List and Filter Beekeepers", description = "Paginated list with optional status and name/KVIC ID search filters")
    public ResponseEntity<ApiResponse<PageResponse<AdminBeekeeperResponse>>> getBeekeepers(
            @RequestParam(required = false) BeekeeperVerificationStatus status,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {
        PageResponse<AdminBeekeeperResponse> response = adminBeekeeperService.getBeekeepers(status, search, pageable);
        return ResponseEntity.ok(ApiResponse.success("Beekeepers retrieved successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Beekeeper Profile Details", description = "Full profile information including hive, batch, and product counts")
    public ResponseEntity<ApiResponse<AdminBeekeeperResponse>> getBeekeeperDetails(@PathVariable Long id) {
        AdminBeekeeperResponse response = adminBeekeeperService.getBeekeeperDetails(id);
        return ResponseEntity.ok(ApiResponse.success("Beekeeper details retrieved successfully", response));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update Beekeeper Status", description = "Approve or reject a pending beekeeper profile")
    public ResponseEntity<ApiResponse<AdminBeekeeperResponse>> updateBeekeeperStatus(
            @PathVariable Long id,
            @Valid @RequestBody AdminBeekeeperStatusUpdateRequest request) {
        AdminBeekeeperResponse response = adminBeekeeperService.updateBeekeeperStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Beekeeper status updated successfully", response));
    }
}
