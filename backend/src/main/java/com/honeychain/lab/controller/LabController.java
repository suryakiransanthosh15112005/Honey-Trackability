package com.honeychain.lab.controller;

import com.honeychain.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/lab")
@Tag(name = "Lab Testing", description = "Endpoints restricted to LAB role")
@SecurityRequirement(name = "Bearer Authentication")
public class LabController {

    @GetMapping("/profile")
    @Operation(summary = "Lab Profile", description = "Returns the authenticated lab technician's information")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProfile(Authentication authentication) {
        Map<String, Object> data = Map.of(
                "phoneNumber", authentication.getName(),
                "role", "LAB",
                "message", "Welcome to Lab Testing Portal");
        return ResponseEntity.ok(ApiResponse.success("Lab profile loaded successfully", data));
    }
}
