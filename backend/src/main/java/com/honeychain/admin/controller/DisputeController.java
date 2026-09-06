package com.honeychain.admin.controller;

import com.honeychain.admin.dto.CreateDisputeRequest;
import com.honeychain.admin.dto.DisputeResponse;
import com.honeychain.admin.service.CustomerDisputeService;
import com.honeychain.common.dto.ApiResponse;
import com.honeychain.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/disputes")
@Tag(name = "Customer Disputes", description = "Customer dispute creation and tracking endpoints")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('CUSTOMER')")
public class DisputeController {

    private final CustomerDisputeService customerDisputeService;

    public DisputeController(CustomerDisputeService customerDisputeService) {
        this.customerDisputeService = customerDisputeService;
    }

    @PostMapping
    @Operation(summary = "Submit Dispute", description = "Customer submits an authenticity or order dispute")
    public ResponseEntity<ApiResponse<DisputeResponse>> createDispute(
            Authentication authentication,
            @Valid @RequestBody CreateDisputeRequest request) {
        DisputeResponse response = customerDisputeService.createDispute(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Dispute submitted successfully", response));
    }

    @GetMapping("/my")
    @Operation(summary = "My Disputes", description = "Returns paginated list of disputes submitted by authenticated customer")
    public ResponseEntity<ApiResponse<PageResponse<DisputeResponse>>> getMyDisputes(
            Authentication authentication,
            @PageableDefault(size = 20) Pageable pageable) {
        PageResponse<DisputeResponse> response = customerDisputeService.getMyDisputes(authentication.getName(), pageable);
        return ResponseEntity.ok(ApiResponse.success("My disputes loaded successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get My Dispute Details", description = "Returns single dispute record for authenticated customer")
    public ResponseEntity<ApiResponse<DisputeResponse>> getDisputeById(
            Authentication authentication,
            @PathVariable Long id) {
        DisputeResponse response = customerDisputeService.getDisputeById(authentication.getName(), id);
        return ResponseEntity.ok(ApiResponse.success("Dispute details loaded successfully", response));
    }
}
