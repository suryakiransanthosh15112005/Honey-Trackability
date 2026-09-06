package com.honeychain.admin.controller;

import com.honeychain.batch.repository.HoneyBatchRepository;
import com.honeychain.beekeeper.repository.BeekeeperProfileRepository;
import com.honeychain.common.dto.ApiResponse;
import com.honeychain.hive.repository.HiveRepository;
import com.honeychain.lab.repository.LabTestRepository;
import com.honeychain.user.repository.UserRepository;
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
@RequestMapping("/api/admin")
@Tag(name = "Admin & KVIC Portal", description = "Endpoints restricted to ADMIN and KVIC_OFFICER roles")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminController {

    private final UserRepository userRepository;
    private final BeekeeperProfileRepository beekeeperProfileRepository;
    private final HiveRepository hiveRepository;
    private final HoneyBatchRepository honeyBatchRepository;
    private final LabTestRepository labTestRepository;

    public AdminController(UserRepository userRepository,
            BeekeeperProfileRepository beekeeperProfileRepository,
            HiveRepository hiveRepository,
            HoneyBatchRepository honeyBatchRepository,
            LabTestRepository labTestRepository) {
        this.userRepository = userRepository;
        this.beekeeperProfileRepository = beekeeperProfileRepository;
        this.hiveRepository = hiveRepository;
        this.honeyBatchRepository = honeyBatchRepository;
        this.labTestRepository = labTestRepository;
    }

    @GetMapping("/profile")
    @Operation(summary = "Admin Profile", description = "Returns the authenticated administrator's information")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProfile(Authentication authentication) {
        Map<String, Object> data = Map.of(
                "phoneNumber", authentication.getName(),
                "authorities", authentication.getAuthorities().toString(),
                "message", "Welcome to Admin & Officer Control Center");
        return ResponseEntity.ok(ApiResponse.success("Admin profile loaded successfully", data));
    }

    @GetMapping("/stats")
    @Operation(summary = "System Statistics", description = "Returns real-time database counts for registered users, beekeepers, hives, batches, and lab tests")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemStats() {
        Map<String, Object> stats = Map.of(
                "totalUsers", userRepository.count(),
                "totalBeekeepers", beekeeperProfileRepository.count(),
                "totalHives", hiveRepository.count(),
                "totalBatches", honeyBatchRepository.count(),
                "totalLabTests", labTestRepository.count());
        return ResponseEntity.ok(ApiResponse.success("System statistics loaded successfully", stats));
    }
}
