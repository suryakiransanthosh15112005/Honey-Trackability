package com.honeychain.beekeeper.controller;

import com.honeychain.beekeeper.dto.BeekeeperProfileRequest;
import com.honeychain.beekeeper.dto.BeekeeperProfileResponse;
import com.honeychain.beekeeper.dto.BeekeeperProfileStatusResponse;
import com.honeychain.beekeeper.dto.BeekeeperProfileUpdateRequest;
import com.honeychain.beekeeper.service.BeekeeperProfileService;
import com.honeychain.common.dto.ApiResponse;
import com.honeychain.common.service.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/beekeepers/profile")
@Tag(name = "Beekeeper Profile", description = "Beekeeper onboarding and profile management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class BeekeeperController {

    private final BeekeeperProfileService beekeeperProfileService;
    private final StorageService storageService;

    public BeekeeperController(BeekeeperProfileService beekeeperProfileService, StorageService storageService) {
        this.beekeeperProfileService = beekeeperProfileService;
        this.storageService = storageService;
    }

    @PostMapping
    @Operation(summary = "Create Beekeeper Profile", description = "Onboards authenticated beekeeper with KVIC ID, name, location, and language")
    public ResponseEntity<ApiResponse<BeekeeperProfileResponse>> createProfile(
            Authentication authentication,
            @Valid @RequestBody BeekeeperProfileRequest request) {
        BeekeeperProfileResponse response = beekeeperProfileService.createProfile(authentication.getName(), request);
        return new ResponseEntity<>(ApiResponse.success("Profile created successfully", response), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get Beekeeper Profile", description = "Returns the authenticated beekeeper's profile")
    public ResponseEntity<ApiResponse<BeekeeperProfileResponse>> getProfile(Authentication authentication) {
        BeekeeperProfileResponse response = beekeeperProfileService.getProfile(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", response));
    }

    @PutMapping
    @Operation(summary = "Update Beekeeper Profile", description = "Updates editable profile fields (name, village, location, photo, language)")
    public ResponseEntity<ApiResponse<BeekeeperProfileResponse>> updateProfile(
            Authentication authentication,
            @Valid @RequestBody BeekeeperProfileUpdateRequest request) {
        BeekeeperProfileResponse response = beekeeperProfileService.updateProfile(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", response));
    }

    @GetMapping("/status")
    @Operation(summary = "Get Onboarding Status", description = "Returns whether the authenticated beekeeper has completed onboarding")
    public ResponseEntity<ApiResponse<BeekeeperProfileStatusResponse>> getProfileStatus(Authentication authentication) {
        BeekeeperProfileStatusResponse response = beekeeperProfileService.getProfileStatus(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Profile status retrieved", response));
    }

    @PostMapping(value = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload Profile Photo", description = "Uploads a photo for beekeeper profile (JPEG, PNG, WEBP)")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadPhoto(
            Authentication authentication,
            @RequestParam("file") MultipartFile file) {
        String photoUrl = storageService.storeFile(file, "beekeepers");
        return ResponseEntity.ok(ApiResponse.success("Photo uploaded successfully", Map.of("photoUrl", photoUrl)));
    }
}
