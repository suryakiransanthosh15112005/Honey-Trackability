package com.honeychain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeychain.batch.dto.HoneyBatchCreateRequest;
import com.honeychain.batch.entity.BatchStatus;
import com.honeychain.batch.entity.HoneyBatch;
import com.honeychain.batch.repository.HoneyBatchRepository;
import com.honeychain.beekeeper.entity.BeekeeperProfile;
import com.honeychain.beekeeper.repository.BeekeeperProfileRepository;
import com.honeychain.hive.entity.Hive;
import com.honeychain.hive.entity.HiveStatus;
import com.honeychain.hive.repository.HiveRepository;
import com.honeychain.lab.dto.LabTestCreateRequest;
import com.honeychain.lab.entity.LabTestResult;
import com.honeychain.security.jwt.JwtService;
import com.honeychain.user.entity.User;
import com.honeychain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PublicVerificationIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private JwtService jwtService;
    @Autowired private UserRepository userRepository;
    @Autowired private BeekeeperProfileRepository beekeeperProfileRepository;
    @Autowired private HiveRepository hiveRepository;
    @Autowired private HoneyBatchRepository honeyBatchRepository;

    private final String BEEKEEPER_PHONE = "9876543213";
    private final String LAB_PHONE = "9876543212";

    private String beekeeperToken() {
        return jwtService.generateToken(BEEKEEPER_PHONE, "BEEKEEPER");
    }

    private String labToken() {
        return jwtService.generateToken(LAB_PHONE, "LAB");
    }

    private String createPureBatchWithQr() throws Exception {
        User user = userRepository.findByPhoneNumber(BEEKEEPER_PHONE).orElseThrow();
        BeekeeperProfile profile = beekeeperProfileRepository.findByUserId(user.getId()).orElseThrow();
        List<Hive> hives = hiveRepository.findAllByBeekeeperProfileId(profile.getId());
        Hive hive = hives.stream().filter(h -> h.getStatus() == HiveStatus.ACTIVE).findFirst().orElseThrow();

        // 1. Create batch
        HoneyBatchCreateRequest req = new HoneyBatchCreateRequest(
                hive.getId(), LocalDate.now(), new BigDecimal("15.00"));

        MvcResult createRes = mockMvc.perform(post("/api/beekeepers/batches")
                        .header("Authorization", "Bearer " + beekeeperToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();

        String batchId = objectMapper.readTree(createRes.getResponse().getContentAsString())
                .at("/data/batchId").asText();

        // 2. Send for testing
        mockMvc.perform(post("/api/beekeepers/batches/" + batchId + "/send-testing")
                        .header("Authorization", "Bearer " + beekeeperToken()))
                .andExpect(status().isOk());

        // 3. Lab submits PURE test
        LabTestCreateRequest labReq = new LabTestCreateRequest(99, LabTestResult.PURE, "Acacia pure honey");
        mockMvc.perform(post("/api/lab/tests/" + batchId)
                        .header("Authorization", "Bearer " + labToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(labReq)))
                .andExpect(status().isCreated());

        // 4. Generate QR
        mockMvc.perform(post("/api/beekeepers/batches/" + batchId + "/generate-qr")
                        .header("Authorization", "Bearer " + beekeeperToken()))
                .andExpect(status().isCreated());

        return batchId;
    }

    // ── Test 1 ───────────────────────────────────────────────────────────────
    @Test
    @DisplayName("1. Public verification endpoint works without authentication (200)")
    void testPublicVerificationNoAuthRequired() throws Exception {
        String batchId = createPureBatchWithQr();

        // Unauthenticated GET request to /api/public/verify/{batchId}
        mockMvc.perform(get("/api/public/verify/" + batchId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.verified", is(true)))
                .andExpect(jsonPath("$.data.verificationStatus", is("GENUINE")))
                .andExpect(jsonPath("$.data.batchId", is(batchId)));
    }

    // ── Test 2 ───────────────────────────────────────────────────────────────
    @Test
    @DisplayName("2. Verified batch returns beekeeper info, purity 99%, blockchain details, and timeline")
    void testVerifiedBatchDetails() throws Exception {
        String batchId = createPureBatchWithQr();

        mockMvc.perform(get("/api/public/verify/" + batchId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.beekeeper.name", notNullValue()))
                .andExpect(jsonPath("$.data.beekeeper.village", notNullValue()))
                .andExpect(jsonPath("$.data.purity.score", is(99)))
                .andExpect(jsonPath("$.data.purity.result", is("PURE")))
                .andExpect(jsonPath("$.data.blockchain.verified", is(true)))
                .andExpect(jsonPath("$.data.blockchain.network", is("HONEYCHAIN-MOCKNET")))
                .andExpect(jsonPath("$.data.blockchain.transactionHash", startsWith("0x")))
                .andExpect(jsonPath("$.data.timeline", hasSize(greaterThanOrEqualTo(4))));
    }

    // ── Test 3 ───────────────────────────────────────────────────────────────
    @Test
    @DisplayName("3. Non-existent batch ID returns 200 with verified: false and NOT_FOUND status")
    void testNonExistentBatchVerification() throws Exception {
        mockMvc.perform(get("/api/public/verify/HC-INVALID-9999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.verified", is(false)))
                .andExpect(jsonPath("$.data.verificationStatus", is("NOT_FOUND")));
    }

    // ── Test 4 ───────────────────────────────────────────────────────────────
    @Test
    @DisplayName("4. Database tampering of quantity is detected and verification fails")
    void testTamperDetectionFailsVerification() throws Exception {
        String batchId = createPureBatchWithQr();

        // Simulate malicious DB tamper on quantity
        HoneyBatch batch = honeyBatchRepository.findByBatchId(batchId).orElseThrow();
        batch.setQuantityKg(new BigDecimal("999.00")); // Tampered from 15.00
        honeyBatchRepository.save(batch);

        // Verification must detect hash mismatch
        mockMvc.perform(get("/api/public/verify/" + batchId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.verified", is(false)))
                .andExpect(jsonPath("$.data.verificationStatus", is("FAILED")))
                .andExpect(jsonPath("$.data.message", containsString("tampering detected")));
    }

    // ── Test 5 ───────────────────────────────────────────────────────────────
    @Test
    @DisplayName("5. Sensitive private info (phone number, password, private IDs) not exposed in public response")
    void testPrivacyProtection() throws Exception {
        String batchId = createPureBatchWithQr();

        mockMvc.perform(get("/api/public/verify/" + batchId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.beekeeper.phoneNumber").doesNotExist())
                .andExpect(jsonPath("$.data.beekeeper.password").doesNotExist())
                .andExpect(jsonPath("$.data.beekeeper.userId").doesNotExist());
    }
}
