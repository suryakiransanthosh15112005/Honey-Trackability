package com.honeychain.admin.controller;

import com.honeychain.admin.dto.AdminDashboardResponse;
import com.honeychain.admin.service.AdminDashboardService;
import com.honeychain.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin & KVIC Dashboard", description = "Platform-wide monitoring KPIs for ADMIN and KVIC_OFFICER roles")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get Platform Overview Statistics", description = "Returns live counts and metrics across beekeepers, hives, batches, honey produced, orders, and risks")
    public ResponseEntity<ApiResponse<AdminDashboardResponse>> getDashboard() {
        AdminDashboardResponse stats = adminDashboardService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success("Dashboard statistics loaded successfully", stats));
    }
}
