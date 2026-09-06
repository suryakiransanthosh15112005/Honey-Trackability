package com.honeychain.marketplace.controller;

import com.honeychain.common.dto.ApiResponse;
import com.honeychain.customer.dto.CustomerProfileRequest;
import com.honeychain.customer.dto.CustomerProfileResponse;
import com.honeychain.customer.dto.CustomerProfileStatusResponse;
import com.honeychain.customer.service.CustomerProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customer Marketplace", description = "Endpoints restricted to CUSTOMER role")
@SecurityRequirement(name = "Bearer Authentication")
public class CustomerController {

    private final CustomerProfileService customerProfileService;

    public CustomerController(CustomerProfileService customerProfileService) {
        this.customerProfileService = customerProfileService;
    }

    @GetMapping("/profile")
    @Operation(summary = "Customer Profile", description = "Returns the authenticated customer's profile information")
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> getProfile(Authentication authentication) {
        try {
            CustomerProfileResponse response = customerProfileService.getProfile(authentication.getName());
            return ResponseEntity.ok(ApiResponse.success("Customer profile loaded successfully", response));
        } catch (Exception e) {
            CustomerProfileResponse fallback = new CustomerProfileResponse(
                    null, null, authentication.getName(), "", "", "", "", "", "", "", null, null
            );
            return ResponseEntity.ok(ApiResponse.success("Customer profile initialized", fallback));
        }
    }

    @PostMapping("/profile")
    @Operation(summary = "Create Customer Profile", description = "Creates a new profile for authenticated customer")
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> createProfile(
            Authentication authentication,
            @Valid @RequestBody CustomerProfileRequest request) {
        CustomerProfileResponse response = customerProfileService.createProfile(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Customer profile created successfully", response));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update Customer Profile", description = "Updates profile details for authenticated customer")
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> updateProfile(
            Authentication authentication,
            @Valid @RequestBody CustomerProfileRequest request) {
        CustomerProfileResponse response = customerProfileService.updateProfile(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Customer profile updated successfully", response));
    }

    @GetMapping("/profile/status")
    @Operation(summary = "Customer Profile Status", description = "Returns profile setup completion status for authenticated customer")
    public ResponseEntity<ApiResponse<CustomerProfileStatusResponse>> getProfileStatus(Authentication authentication) {
        CustomerProfileStatusResponse response = customerProfileService.getStatus(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Customer profile status loaded successfully", response));
    }
}
