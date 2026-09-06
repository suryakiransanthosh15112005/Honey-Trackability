package com.honeychain.admin.controller;

import com.honeychain.admin.dto.AdminHiveResponse;
import com.honeychain.admin.service.AdminHiveService;
import com.honeychain.common.dto.ApiResponse;
import com.honeychain.common.dto.PageResponse;
import com.honeychain.hive.entity.HiveStatus;
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
@RequestMapping("/api/admin/hives")
@Tag(name = "Admin Hive & IoT Monitoring", description = "Hive lifecycle, status, and IoT health monitoring")
public class AdminHiveController {

    private final AdminHiveService adminHiveService;

    public AdminHiveController(AdminHiveService adminHiveService) {
        this.adminHiveService = adminHiveService;
    }

    @GetMapping
    @Operation(summary = "List Hives with IoT Health", description = "Paginated list of all hives across beekeepers with real-time IoT metrics")
    public ResponseEntity<ApiResponse<PageResponse<AdminHiveResponse>>> getHives(
            @RequestParam(required = false) HiveStatus status,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {
        PageResponse<AdminHiveResponse> response = adminHiveService.getHives(status, search, pageable);
        return ResponseEntity.ok(ApiResponse.success("Hives retrieved successfully", response));
    }
}
