package com.honeychain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeychain.batch.dto.HoneyBatchCreateRequest;
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
import com.honeychain.verification.entity.VerificationRiskLevel;
import com.honeychain.verification.repository.VerificationHistoryRepository;
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
class VerificationHistoryIntegrationTest {

        @Autowired
        private MockMvc mockMvc;
        @Autowired
        private ObjectMapper objectMapper;
        @Autowired
        private JwtService jwtService;
        @Autowired
        private UserRepository userRepository;
        @Autowired
        private BeekeeperProfileRepository beekeeperProfileRepository;
        @Autowired
        private HiveRepository hiveRepository;
        @Autowired
        private HoneyBatchRepository honeyBatchRepository;
        @Autowired
        private VerificationHistoryRepository verificationHistoryRepository;

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
                                hive.getId(), LocalDate.now(), new BigDecimal("14.00"));

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
                LabTestCreateRequest labReq = new LabTestCreateRequest(97, LabTestResult.PURE, "Pure floral honey");
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
        @DisplayName("1. Public POST scan endpoint records verification event without authentication (200)")
        void testRecordScanPublic() throws Exception {
                String batchId = createPureBatchWithQr();

                mockMvc.perform(post("/api/public/verify/" + batchId + "/scan")
                                .header("User-Agent", "HoneyChain-MobileApp/1.0")
                                .header("X-Forwarded-For", "203.0.113.195"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.data.verified", is(true)))
                                .andExpect(jsonPath("$.data.verificationHistory.totalVerifications",
                                                greaterThanOrEqualTo(1)))
                                .andExpect(jsonPath("$.data.verificationHistory.riskLevel", is("NORMAL")));

                long total = verificationHistoryRepository.countByBatchId(batchId);
                assertTrue(total >= 1);
        }

        // ── Test 2 ───────────────────────────────────────────────────────────────
        @Test
        @DisplayName("2. Debounce prevents duplicate scan record from same fingerprint in rapid succession")
        void testDebounceProtection() throws Exception {
                String batchId = createPureBatchWithQr();

                // First scan
                mockMvc.perform(post("/api/public/verify/" + batchId + "/scan")
                                .header("User-Agent", "Mozilla/5.0 Safari/537.36")
                                .header("X-Forwarded-For", "198.51.100.42"))
                                .andExpect(status().isOk());

                long countAfterFirst = verificationHistoryRepository.countByBatchId(batchId);

                // Immediate duplicate scan (within 5 seconds)
                mockMvc.perform(post("/api/public/verify/" + batchId + "/scan")
                                .header("User-Agent", "Mozilla/5.0 Safari/537.36")
                                .header("X-Forwarded-For", "198.51.100.42"))
                                .andExpect(status().isOk());

                long countAfterSecond = verificationHistoryRepository.countByBatchId(batchId);
                assertEquals(countAfterFirst, countAfterSecond,
                                "Debounce should prevent immediate duplicate row creation");
        }

        // ── Test 3 ───────────────────────────────────────────────────────────────
        @Test
        @DisplayName("3. Public history endpoint returns scan count, risk level, and events without sensitive data")
        void testGetPublicVerificationHistory() throws Exception {
                String batchId = createPureBatchWithQr();

                // Perform scan
                mockMvc.perform(post("/api/public/verify/" + batchId + "/scan"))
                                .andExpect(status().isOk());

                // Query public history
                mockMvc.perform(get("/api/public/verify/" + batchId + "/history"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.data.totalVerifications", greaterThanOrEqualTo(1)))
                                .andExpect(jsonPath("$.data.riskLevel", notNullValue()))
                                .andExpect(jsonPath("$.data.recentEvents", hasSize(greaterThanOrEqualTo(1))))
                                .andExpect(jsonPath("$.data.recentEvents[0].result", is("VERIFIED")))
                                // Ensure no private leak
                                .andExpect(jsonPath("$.data.recentEvents[0].verificationFingerprint").doesNotExist())
                                .andExpect(jsonPath("$.data.recentEvents[0].customerId").doesNotExist());
        }

        // ── Test 4 ───────────────────────────────────────────────────────────────
        @Test
        @DisplayName("4. Beekeeper can view verification activity for owned batch (200)")
        void testBeekeeperViewHistory() throws Exception {
                String batchId = createPureBatchWithQr();

                mockMvc.perform(get("/api/beekeepers/batches/" + batchId + "/verification-history")
                                .header("Authorization", "Bearer " + beekeeperToken()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.data.totalVerifications", greaterThanOrEqualTo(0)))
                                .andExpect(jsonPath("$.data.riskLevel", notNullValue()));
        }
}
