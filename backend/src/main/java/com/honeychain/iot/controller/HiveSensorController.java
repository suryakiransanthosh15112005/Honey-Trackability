package com.honeychain.iot.controller;

import com.honeychain.beekeeper.entity.BeekeeperProfile;
import com.honeychain.beekeeper.repository.BeekeeperProfileRepository;
import com.honeychain.common.dto.ApiResponse;
import com.honeychain.common.exception.BadRequestException;
import com.honeychain.common.exception.ResourceNotFoundException;
import com.honeychain.hive.entity.Hive;
import com.honeychain.hive.repository.HiveRepository;
import com.honeychain.iot.dto.HiveHealthResponse;
import com.honeychain.iot.dto.SensorDataResponse;
import com.honeychain.iot.dto.SensorHistoryResponse;
import com.honeychain.iot.entity.HiveSensorData;
import com.honeychain.iot.mapper.HiveSensorDataMapper;
import com.honeychain.iot.service.HiveHealthService;
import com.honeychain.iot.service.IoTService;
import com.honeychain.user.entity.User;
import com.honeychain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/beekeepers/hives")
@Tag(name = "IoT Hive Health", description = "Simulated IoT sensor data and hive health monitoring APIs for beekeepers")
@SecurityRequirement(name = "Bearer Authentication")
public class HiveSensorController {

    private final IoTService iotService;
    private final HiveHealthService hiveHealthService;
    private final HiveRepository hiveRepository;
    private final BeekeeperProfileRepository beekeeperProfileRepository;
    private final UserService userService;
    private final HiveSensorDataMapper sensorDataMapper;

    public HiveSensorController(IoTService iotService,
            HiveHealthService hiveHealthService,
            HiveRepository hiveRepository,
            BeekeeperProfileRepository beekeeperProfileRepository,
            UserService userService,
            HiveSensorDataMapper sensorDataMapper) {
        this.iotService = iotService;
        this.hiveHealthService = hiveHealthService;
        this.hiveRepository = hiveRepository;
        this.beekeeperProfileRepository = beekeeperProfileRepository;
        this.userService = userService;
        this.sensorDataMapper = sensorDataMapper;
    }

    // ── All Hives Health Summary ──────────────────────────────────────────────

    @GetMapping("/health")
    @Operation(summary = "Get Health for All Hives", description = "Returns HEALTHY/WATCH/ALERT analysis for all hives owned by the authenticated beekeeper")
    public ResponseEntity<ApiResponse<List<HiveHealthResponse>>> getAllHivesHealth(Authentication authentication) {
        List<HiveHealthResponse> responses = hiveHealthService.analyzeAllHivesHealth(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Hive health retrieved for all hives", responses));
    }

    // ── Single Hive Health ────────────────────────────────────────────────────

    @GetMapping("/{hiveId}/health")
    @Operation(summary = "Get Health for Single Hive", description = "Returns HEALTHY/WATCH/ALERT health analysis with plain-language message for a specific owned hive")
    public ResponseEntity<ApiResponse<HiveHealthResponse>> getHiveHealth(
            Authentication authentication,
            @PathVariable Long hiveId) {
        verifyOwnership(authentication.getName(), hiveId);
        HiveHealthResponse response = hiveHealthService.analyzeHealth(hiveId);
        return ResponseEntity.ok(ApiResponse.success("Hive health analysis complete", response));
    }

    // ── Latest Sensor Reading ─────────────────────────────────────────────────

    @GetMapping("/{hiveId}/sensors/latest")
    @Operation(summary = "Get Latest Sensor Reading", description = "Returns the most recent IoT sensor data (temperature, humidity, bee activity) for an owned hive")
    public ResponseEntity<ApiResponse<SensorDataResponse>> getLatestSensorData(
            Authentication authentication,
            @PathVariable Long hiveId) {
        Hive hive = verifyOwnership(authentication.getName(), hiveId);
        Optional<HiveSensorData> latestOpt = iotService.getLatestSensorData(hiveId);

        // Generate on-demand if no reading exists yet (first access)
        HiveSensorData data = latestOpt.orElseGet(() -> iotService.generateMockReading(hiveId));
        SensorDataResponse response = sensorDataMapper.toResponse(data, hive);

        return ResponseEntity.ok(ApiResponse.success("Latest sensor reading retrieved", response));
    }

    // ── Sensor History ────────────────────────────────────────────────────────

    @GetMapping("/{hiveId}/sensors/history")
    @Operation(summary = "Get Sensor History", description = "Returns paginated IoT sensor history for an owned hive (newest first, default 20 readings per page)")
    public ResponseEntity<ApiResponse<SensorHistoryResponse>> getSensorHistory(
            Authentication authentication,
            @PathVariable Long hiveId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        if (size > 100)
            size = 100; // Hard cap to prevent abuse
        Hive hive = verifyOwnership(authentication.getName(), hiveId);

        List<HiveSensorData> readings = iotService.getSensorHistory(hiveId, PageRequest.of(page, size));
        List<SensorDataResponse> mapped = sensorDataMapper.toResponseList(readings, hive);

        SensorHistoryResponse response = new SensorHistoryResponse(
                hiveId,
                hive.getHiveCode(),
                mapped.size(),
                mapped);

        return ResponseEntity.ok(ApiResponse.success("Sensor history retrieved", response));
    }

    // ── Ownership Verification ────────────────────────────────────────────────

    private Hive verifyOwnership(String phoneNumber, Long hiveId) {
        User user = userService.findEntityByPhoneNumber(phoneNumber);
        BeekeeperProfile profile = beekeeperProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BadRequestException("Beekeeper profile not found"));

        return hiveRepository.findByIdAndBeekeeperProfileId(hiveId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Hive", "id", hiveId));
    }
}
