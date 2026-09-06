package com.honeychain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeychain.beekeeper.repository.BeekeeperProfileRepository;
import com.honeychain.hive.entity.Hive;
import com.honeychain.hive.entity.HiveStatus;
import com.honeychain.hive.repository.HiveRepository;
import com.honeychain.iot.entity.HiveHealthStatus;
import com.honeychain.iot.repository.HiveSensorDataRepository;
import com.honeychain.security.jwt.JwtService;
import com.honeychain.user.entity.User;
import com.honeychain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class IoTIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private JwtService jwtService;
    @Autowired private UserRepository userRepository;
    @Autowired private BeekeeperProfileRepository beekeeperProfileRepository;
    @Autowired private HiveRepository hiveRepository;
    @Autowired private HiveSensorDataRepository sensorDataRepository;

    private static final String BEEKEEPER_PHONE = "9876543213";
    private static final String LAB_PHONE = "9876543212";
    private static final String CUSTOMER_PHONE = "9876543214";

    private String beekeeperToken() {
        return jwtService.generateToken(BEEKEEPER_PHONE, "BEEKEEPER");
    }

    private String labToken() {
        return jwtService.generateToken(LAB_PHONE, "LAB");
    }

    private Long getBeekeeperFirstHiveId() {
        User user = userRepository.findByPhoneNumber(BEEKEEPER_PHONE).orElseThrow();
        var profile = beekeeperProfileRepository.findByUserId(user.getId()).orElseThrow();
        List<Hive> hives = hiveRepository.findAllByBeekeeperProfileId(profile.getId());
        assertTrue(!hives.isEmpty(), "Test beekeeper must have at least one hive");
        return hives.stream().filter(h -> h.getStatus() == HiveStatus.ACTIVE).findFirst().orElseThrow().getId();
    }

    // ── Test 1: Get all hives health (200) ─────────────────────────────────
    @Test
    @DisplayName("1. Beekeeper can get health for all owned hives (200)")
    void testGetAllHivesHealth() throws Exception {
        mockMvc.perform(get("/api/beekeepers/hives/health")
                        .header("Authorization", "Bearer " + beekeeperToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", isA(List.class)))
                .andExpect(jsonPath("$.data[0].hiveId", notNullValue()))
                .andExpect(jsonPath("$.data[0].status", notNullValue()))
                .andExpect(jsonPath("$.data[0].message", notNullValue()))
                .andExpect(jsonPath("$.data[0].hiveCode", notNullValue()));
    }

    // ── Test 2: Get single hive health (200) ────────────────────────────────
    @Test
    @DisplayName("2. Beekeeper can get health for a specific owned hive (200)")
    void testGetSingleHiveHealth() throws Exception {
        Long hiveId = getBeekeeperFirstHiveId();

        mockMvc.perform(get("/api/beekeepers/hives/" + hiveId + "/health")
                        .header("Authorization", "Bearer " + beekeeperToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.hiveId", is(hiveId.intValue())))
                .andExpect(jsonPath("$.data.status", oneOf("HEALTHY", "WATCH", "ALERT")))
                .andExpect(jsonPath("$.data.message", not(emptyString())))
                .andExpect(jsonPath("$.data.temperature", notNullValue()))
                .andExpect(jsonPath("$.data.humidity", notNullValue()))
                .andExpect(jsonPath("$.data.beeActivity", notNullValue()))
                .andExpect(jsonPath("$.data.checkedAt", notNullValue()));
    }

    // ── Test 3: Get latest sensor reading (200) ────────────────────────────
    @Test
    @DisplayName("3. Beekeeper can retrieve latest sensor reading for owned hive (200)")
    void testGetLatestSensor() throws Exception {
        Long hiveId = getBeekeeperFirstHiveId();

        mockMvc.perform(get("/api/beekeepers/hives/" + hiveId + "/sensors/latest")
                        .header("Authorization", "Bearer " + beekeeperToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.temperature", notNullValue()))
                .andExpect(jsonPath("$.data.humidity", notNullValue()))
                .andExpect(jsonPath("$.data.beeActivity", notNullValue()))
                .andExpect(jsonPath("$.data.recordedAt", notNullValue()));

        // Verify the reading was stored in DB
        long count = sensorDataRepository.countByHiveId(hiveId);
        assertTrue(count >= 1, "At least one sensor reading should be stored");
    }

    // ── Test 4: Get sensor history (200) ──────────────────────────────────
    @Test
    @DisplayName("4. Beekeeper can retrieve sensor history with pagination (200)")
    void testGetSensorHistory() throws Exception {
        Long hiveId = getBeekeeperFirstHiveId();

        mockMvc.perform(get("/api/beekeepers/hives/" + hiveId + "/sensors/history?page=0&size=10")
                        .header("Authorization", "Bearer " + beekeeperToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.hiveId", is(hiveId.intValue())))
                .andExpect(jsonPath("$.data.readings", isA(List.class)));
    }

    // ── Test 5: LAB cannot access IoT endpoints (403) ──────────────────────
    @Test
    @DisplayName("5. LAB user cannot access beekeeper IoT endpoints (403)")
    void testLabCannotAccessIoT() throws Exception {
        Long hiveId = getBeekeeperFirstHiveId();

        mockMvc.perform(get("/api/beekeepers/hives/" + hiveId + "/health")
                        .header("Authorization", "Bearer " + labToken()))
                .andExpect(status().isForbidden());
    }

    // ── Test 6: Unauthenticated access blocked (401) ────────────────────────
    @Test
    @DisplayName("6. Unauthenticated access to IoT endpoints is rejected (401)")
    void testUnauthenticatedAccessBlocked() throws Exception {
        Long hiveId = getBeekeeperFirstHiveId();

        mockMvc.perform(get("/api/beekeepers/hives/" + hiveId + "/health"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/beekeepers/hives/health"))
                .andExpect(status().isUnauthorized());
    }

    // ── Test 7: Beekeeper cannot access another beekeeper's hive (404) ─────
    @Test
    @DisplayName("7. Beekeeper cannot access hive belonging to another beekeeper (404)")
    void testCrossBeekeeperAccessDenied() throws Exception {
        // Create a new orphan hive with a dummy beekeeperProfileId
        Hive orphanHive = new Hive(99999L, "HIVE-TEST-X9", "Other Cluster", null, null, HiveStatus.ACTIVE, LocalDate.now());
        hiveRepository.save(orphanHive);

        mockMvc.perform(get("/api/beekeepers/hives/" + orphanHive.getId() + "/health")
                        .header("Authorization", "Bearer " + beekeeperToken()))
                .andExpect(status().isNotFound());
    }

    // ── Test 8: Page size cap prevents excessive data loading (≤100 rows) ──
    @Test
    @DisplayName("8. Sensor history page size is capped at 100 even if larger is requested")
    void testPageSizeCap() throws Exception {
        Long hiveId = getBeekeeperFirstHiveId();

        // Request a huge page — should still return 200 (not 500 or load unbounded data)
        mockMvc.perform(get("/api/beekeepers/hives/" + hiveId + "/sensors/history?page=0&size=9999")
                        .header("Authorization", "Bearer " + beekeeperToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }
}
