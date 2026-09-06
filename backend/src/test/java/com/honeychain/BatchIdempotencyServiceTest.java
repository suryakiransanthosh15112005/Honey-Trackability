package com.honeychain;

import com.honeychain.batch.dto.HoneyBatchCreateRequest;
import com.honeychain.batch.entity.BatchCreationRequest;
import com.honeychain.batch.repository.BatchCreationRequestRepository;
import com.honeychain.batch.repository.HoneyBatchRepository;
import com.honeychain.beekeeper.entity.BeekeeperProfile;
import com.honeychain.beekeeper.entity.BeekeeperVerificationStatus;
import com.honeychain.beekeeper.entity.PreferredLanguage;
import com.honeychain.beekeeper.repository.BeekeeperProfileRepository;
import com.honeychain.hive.entity.Hive;
import com.honeychain.hive.entity.HiveStatus;
import com.honeychain.hive.repository.HiveRepository;
import com.honeychain.batch.service.HoneyBatchService;
import com.honeychain.security.jwt.JwtService;
import com.honeychain.user.entity.Role;
import com.honeychain.user.entity.User;
import com.honeychain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for idempotent batch creation via X-Idempotency-Key header.
 *
 * Tests:
 *  1. Same idempotency key creates only one batch.
 *  2. Retried request returns the same batch (same batchId).
 *  3. Different idempotency key creates a different batch.
 *  4. Idempotency key is scoped to the authenticated beekeeper.
 *  5. A different beekeeper cannot retrieve another beekeeper's batch via their key.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BatchIdempotencyServiceTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtService jwtService;
    @Autowired private UserRepository userRepository;
    @Autowired private BeekeeperProfileRepository beekeeperProfileRepository;
    @Autowired private HiveRepository hiveRepository;
    @Autowired private HoneyBatchRepository honeyBatchRepository;
    @Autowired private BatchCreationRequestRepository batchCreationRequestRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private static final String PHONE_A = "9876540101";
    private static final String PHONE_B = "9876540102";

    private Long hiveIdA;
    private Long hiveIdB;

    @BeforeEach
    void setup() {
        hiveIdA = ensureBeekeeperWithHive(PHONE_A, "BKIDM-A");
        hiveIdB = ensureBeekeeperWithHive(PHONE_B, "BKIDM-B");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Test 1: Same key → exactly ONE batch row created
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("Same idempotency key creates only one batch")
    void sameKeyCreatesOneBatch() throws Exception {
        String key = "LOCAL-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String token = jwtService.generateToken(PHONE_A, "BEEKEEPER");
        String body = buildJsonRequest(hiveIdA);

        // First request → 201 Created
        mockMvc.perform(post("/api/beekeepers/batches")
                        .header("Authorization", "Bearer " + token)
                        .header("X-Idempotency-Key", key)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        long countBefore = honeyBatchRepository.count();

        // Second request with same key → should NOT create a second batch
        mockMvc.perform(post("/api/beekeepers/batches")
                        .header("Authorization", "Bearer " + token)
                        .header("X-Idempotency-Key", key)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().is2xxSuccessful());

        long countAfter = honeyBatchRepository.count();
        assertThat(countAfter).isEqualTo(countBefore);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Test 2: Retry returns the SAME batchId
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("Retried request returns the same official batch ID")
    void retryReturnsSameBatchId() throws Exception {
        String key = "LOCAL-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String token = jwtService.generateToken(PHONE_A, "BEEKEEPER");
        String body = buildJsonRequest(hiveIdA);

        // First creation
        String firstResponse = mockMvc.perform(post("/api/beekeepers/batches")
                        .header("Authorization", "Bearer " + token)
                        .header("X-Idempotency-Key", key)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // Extract batchId from first response
        String batchId1 = extractBatchId(firstResponse);

        // Retry
        String secondResponse = mockMvc.perform(post("/api/beekeepers/batches")
                        .header("Authorization", "Bearer " + token)
                        .header("X-Idempotency-Key", key)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();

        String batchId2 = extractBatchId(secondResponse);
        assertThat(batchId2).isEqualTo(batchId1);
    }


    // ─────────────────────────────────────────────────────────────────────────
    // Test 3: Different idempotency key → different batch
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("Different idempotency keys create different batches")
    void differentKeysCreateDifferentBatches() throws Exception {
        String key1 = "LOCAL-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String key2 = "LOCAL-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String token = jwtService.generateToken(PHONE_A, "BEEKEEPER");
        String body = buildJsonRequest(hiveIdA);

        String resp1 = mockMvc.perform(post("/api/beekeepers/batches")
                        .header("Authorization", "Bearer " + token)
                        .header("X-Idempotency-Key", key1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String resp2 = mockMvc.perform(post("/api/beekeepers/batches")
                        .header("Authorization", "Bearer " + token)
                        .header("X-Idempotency-Key", key2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        assertThat(extractBatchId(resp1)).isNotEqualTo(extractBatchId(resp2));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Test 4: Key is scoped — beekeeper A's key does not affect beekeeper B
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("Idempotency key is scoped to the authenticated beekeeper")
    void keyIsScopedToBeekeeper() throws Exception {
        String key = "LOCAL-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String tokenA = jwtService.generateToken(PHONE_A, "BEEKEEPER");
        String tokenB = jwtService.generateToken(PHONE_B, "BEEKEEPER");

        // Beekeeper A creates batch with key
        mockMvc.perform(post("/api/beekeepers/batches")
                        .header("Authorization", "Bearer " + tokenA)
                        .header("X-Idempotency-Key", key)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildJsonRequest(hiveIdA)))
                .andExpect(status().isCreated());

        // Beekeeper B uses the SAME key — should create a NEW batch (not the same one)
        // because keys are scoped per beekeeper profile
        String respB = mockMvc.perform(post("/api/beekeepers/batches")
                        .header("Authorization", "Bearer " + tokenB)
                        .header("X-Idempotency-Key", key)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildJsonRequest(hiveIdB)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // Beekeeper B should get a unique new batchId, not beekeeper A's batch
        assertThat(extractBatchId(respB)).startsWith("HC-");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Test 5: Cross-user idempotency key replay is rejected (scoping prevents leak)
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("Malicious replay of another beekeeper's key does not return their batch")
    void maliciousReplayDoesNotLeakData() throws Exception {
        String key = "LOCAL-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String tokenA = jwtService.generateToken(PHONE_A, "BEEKEEPER");
        String tokenB = jwtService.generateToken(PHONE_B, "BEEKEEPER");

        // Beekeeper A creates batch
        String respA = mockMvc.perform(post("/api/beekeepers/batches")
                        .header("Authorization", "Bearer " + tokenA)
                        .header("X-Idempotency-Key", key)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildJsonRequest(hiveIdA)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String batchIdA = extractBatchId(respA);

        // Beekeeper B sends the same key — they get their OWN new batch, NOT beekeeper A's
        String respB = mockMvc.perform(post("/api/beekeepers/batches")
                        .header("Authorization", "Bearer " + tokenB)
                        .header("X-Idempotency-Key", key)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildJsonRequest(hiveIdB)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String batchIdB = extractBatchId(respB);

        // Different beekeepers must always get different batches
        assertThat(batchIdB).isNotEqualTo(batchIdA);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private String buildJsonRequest(Long hiveId) {
        return "{\"hiveId\":" + hiveId + ",\"harvestDate\":\"" + LocalDate.now() + "\",\"quantityKg\":5.00}";
    }

    private String extractBatchId(String json) {
        // Quick extraction: find "batchId":"HC-2026-XXXXXXXX"
        int idx = json.indexOf("\"batchId\":\"");
        if (idx < 0) return "";
        int start = idx + 11;
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }

    private Long ensureBeekeeperWithHive(String phone, String kvicId) {
        User user = userRepository.findByPhoneNumber(phone).orElseGet(() -> {
            User u = new User(phone, passwordEncoder.encode("Test@1234"), Role.BEEKEEPER, true);
            return userRepository.save(u);
        });

        BeekeeperProfile profile = beekeeperProfileRepository.findByUserId(user.getId()).orElseGet(() -> {
            BeekeeperProfile p = new BeekeeperProfile(
                user.getId(),
                kvicId,
                "Beekeeper " + kvicId,
                "Test Village",
                null,
                null,
                null,
                PreferredLanguage.ENGLISH,
                BeekeeperVerificationStatus.APPROVED
            );

            return beekeeperProfileRepository.save(p);
        });

        Hive hive = hiveRepository.findAllByBeekeeperProfileId(profile.getId())
                .stream().filter(h -> h.getStatus() == HiveStatus.ACTIVE).findFirst()
                .orElseGet(() -> {
                    Hive h = new Hive();
                    h.setBeekeeperProfileId(profile.getId());
                    h.setHiveCode("HIDM-" + kvicId);
                    h.setClusterName("Test Cluster");
                    h.setStatus(HiveStatus.ACTIVE);
                    h.setInstalledDate(LocalDate.now().minusDays(30));
                    return hiveRepository.save(h);
                });

        return hive.getId();
    }

}
