package com.honeychain.hive.controller;

import com.honeychain.common.dto.ApiResponse;
import com.honeychain.hive.dto.HiveCreateRequest;
import com.honeychain.hive.dto.HiveResponse;
import com.honeychain.hive.dto.HiveStatusUpdateRequest;
import com.honeychain.hive.dto.HiveUpdateRequest;
import com.honeychain.hive.service.HiveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/beekeepers/hives")
@Tag(name = "Hive Management", description = "Beekeeper hive registration and management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class HiveController {

    private final HiveService hiveService;

    public HiveController(HiveService hiveService) {
        this.hiveService = hiveService;
    }

    @PostMapping
    @Operation(summary = "Create Hive", description = "Register a new hive for the authenticated beekeeper")
    public ResponseEntity<ApiResponse<HiveResponse>> createHive(
            Authentication authentication,
            @Valid @RequestBody HiveCreateRequest request) {
        HiveResponse response = hiveService.createHive(authentication.getName(), request);
        return new ResponseEntity<>(
                ApiResponse.success("Hive " + response.getHiveCode() + " created successfully", response),
                HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get All My Hives", description = "Returns all hives belonging to the authenticated beekeeper")
    public ResponseEntity<ApiResponse<List<HiveResponse>>> getMyHives(Authentication authentication) {
        List<HiveResponse> hives = hiveService.getMyHives(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Hives retrieved successfully", hives));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Single Hive", description = "Returns a specific hive owned by the authenticated beekeeper")
    public ResponseEntity<ApiResponse<HiveResponse>> getMyHive(
            Authentication authentication,
            @PathVariable Long id) {
        HiveResponse response = hiveService.getMyHive(authentication.getName(), id);
        return ResponseEntity.ok(ApiResponse.success("Hive retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Hive", description = "Update editable hive fields for the authenticated beekeeper")
    public ResponseEntity<ApiResponse<HiveResponse>> updateHive(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody HiveUpdateRequest request) {
        HiveResponse response = hiveService.updateHive(authentication.getName(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Hive updated successfully", response));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update Hive Status", description = "Activate or deactivate a hive (ACTIVE/INACTIVE only; ALERT is IoT-driven)")
    public ResponseEntity<ApiResponse<HiveResponse>> updateHiveStatus(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody HiveStatusUpdateRequest request) {
        HiveResponse response = hiveService.updateHiveStatus(authentication.getName(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Hive status updated to " + response.getStatus(), response));
    }

    @GetMapping("/count")
    @Operation(summary = "Count My Hives", description = "Returns total number of hives for the authenticated beekeeper")
    public ResponseEntity<ApiResponse<Map<String, Long>>> countMyHives(Authentication authentication) {
        long count = hiveService.countMyHives(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Hive count retrieved", Map.of("total", count)));
    }
}
