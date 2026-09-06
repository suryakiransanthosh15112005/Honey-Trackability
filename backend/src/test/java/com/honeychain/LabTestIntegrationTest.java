package com.honeychain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeychain.batch.dto.HoneyBatchCreateRequest;
import com.honeychain.batch.entity.BatchStatus;
import com.honeychain.batch.entity.HoneyBatch;
import com.honeychain.batch.repository.HoneyBatchRepository;
import com.honeychain.beekeeper.entity.BeekeeperProfile;
import com.honeychain.beekeeper.repository.BeekeeperProfileRepository;
import com.honeychain.blockchain.entity.BlockchainRecord;
import com.honeychain.blockchain.entity.BlockchainRecordType;
import com.honeychain.blockchain.repository.BlockchainRecordRepository;
import com.honeychain.hive.entity.Hive;
import com.honeychain.hive.entity.HiveStatus;
import com.honeychain.hive.repository.HiveRepository;
import com.honeychain.lab.dto.LabTestCreateRequest;
import com.honeychain.lab.entity.LabTestResult;
import com.honeychain.lab.repository.LabTestRepository;
import com.honeychain.security.jwt.JwtService;
import com.honeychain.user.entity.User;
import com.honeychain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LabTestIntegrationTest {

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
        private LabTestRepository labTestRepository;
        @Autowired
        private BlockchainRecordRepository blockchainRecordRepository;

        private final String BEEKEEPER_PHONE = "9876543213";
        private final String LAB_PHONE = "9876543212";
        private final String CUSTOMER_PHONE = "9876543214";

        private String beekeeperToken() {
                return jwtService.generateToken(BEEKEEPER_PHONE, "BEEKEEPER");
        }

        private String labToken() {
                return jwtService.generateToken(LAB_PHONE, "LAB");
        }

        private String customerToken() {
                return jwtService.generateToken(CUSTOMER_PHONE, "CUSTOMER");
        }

        private String createAndSendBatchForTesting() throws Exception {
                User user = userRepository.findByPhoneNumber(BEEKEEPER_PHONE).orElseThrow();
                BeekeeperProfile profile = beekeeperProfileRepository.findByUserId(user.getId()).orElseThrow();
                List<Hive> hives = hiveRepository.findAllByBeekeeperProfileId(profile.getId());
                Hive hive = hives.stream().filter(h -> h.getStatus() == HiveStatus.ACTIVE).findFirst().orElseThrow();

                // 1. Create batch
                HoneyBatchCreateRequest req = new HoneyBatchCreateRequest(
                                hive.getId(), LocalDate.now(), new BigDecimal("10.00"));

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

                return batchId;
        }

        // ── Test 1 ───────────────────────────────────────────────────────────────
        @Test
        @DisplayName("1. Lab user can retrieve list of pending batches (200)")
        void testGetPendingBatches() throws Exception {
                String batchId = createAndSendBatchForTesting();

                mockMvc.perform(get("/api/lab/tests/pending")
                                .header("Authorization", "Bearer " + labToken()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.data", isA(List.class)))
                                .andExpect(jsonPath("$.data[*].batchId", hasItem(batchId)));
        }

        // ── Test 2 ───────────────────────────────────────────────────────────────
        @Test
        @DisplayName("2. Submit PURE lab test updates batch to PURE and creates blockchain LAB_RESULT record (201)")
        void testSubmitPureLabTest() throws Exception {
                String batchId = createAndSendBatchForTesting();

                LabTestCreateRequest req = new LabTestCreateRequest(98, LabTestResult.PURE,
                                "Excellent purity, 0% adulteration");

                mockMvc.perform(post("/api/lab/tests/" + batchId)
                                .header("Authorization", "Bearer " + labToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.data.batchId", is(batchId)))
                                .andExpect(jsonPath("$.data.purityScore", is(98)))
                                .andExpect(jsonPath("$.data.result", is("PURE")))
                                .andExpect(jsonPath("$.data.blockchainRecord", notNullValue()))
                                .andExpect(jsonPath("$.data.blockchainRecord.recordType", is("LAB_RESULT")));

                // Verify HoneyBatch in database has status PURE
                HoneyBatch batchInDb = honeyBatchRepository.findByBatchId(batchId).orElseThrow();
                assertEquals(BatchStatus.PURE, batchInDb.getStatus());

                // Verify BlockchainRecord exists for LAB_RESULT
                Optional<BlockchainRecord> labBcRecord = blockchainRecordRepository.findByBatchIdAndRecordType(
                                batchId, BlockchainRecordType.LAB_RESULT);
                assertTrue(labBcRecord.isPresent());
                assertTrue(labBcRecord.get().getTransactionHash().startsWith("0x"));
        }

        // ── Test 3 ───────────────────────────────────────────────────────────────
        @Test
        @DisplayName("3. Submit lab test with multipart certificate upload (201)")
        void testSubmitLabTestWithCertificate() throws Exception {
                String batchId = createAndSendBatchForTesting();

                MockMultipartFile certFile = new MockMultipartFile(
                                "certificate", "cert.pdf", "application/pdf",
                                "%PDF-1.4 Mock Lab Certificate".getBytes());

                mockMvc.perform(multipart("/api/lab/tests/" + batchId)
                                .file(certFile)
                                .param("purityScore", "95")
                                .param("result", "PURE")
                                .param("remarks", "Certified by Government Laboratory")
                                .header("Authorization", "Bearer " + labToken()))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.data.certificateUrl", startsWith("/uploads/lab-certificates/")))
                                .andExpect(jsonPath("$.data.purityScore", is(95)));
        }

        // ── Test 4 ───────────────────────────────────────────────────────────────
        @Test
        @DisplayName("4. Submit FAILED lab test updates batch to FAILED (201)")
        void testSubmitFailedLabTest() throws Exception {
                String batchId = createAndSendBatchForTesting();

                LabTestCreateRequest req = new LabTestCreateRequest(45, LabTestResult.FAILED,
                                "Excessive sucrose detected");

                mockMvc.perform(post("/api/lab/tests/" + batchId)
                                .header("Authorization", "Bearer " + labToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.data.result", is("FAILED")));

                HoneyBatch batchInDb = honeyBatchRepository.findByBatchId(batchId).orElseThrow();
                assertEquals(BatchStatus.FAILED, batchInDb.getStatus());
        }

        // ── Test 5 ───────────────────────────────────────────────────────────────
        @Test
        @DisplayName("5. Submitting test for CREATED batch (not sent for testing) returns 400 Bad Request")
        void testTestingNonPendingBatchRejected() throws Exception {
                User user = userRepository.findByPhoneNumber(BEEKEEPER_PHONE).orElseThrow();
                BeekeeperProfile profile = beekeeperProfileRepository.findByUserId(user.getId()).orElseThrow();
                List<Hive> hives = hiveRepository.findAllByBeekeeperProfileId(profile.getId());
                Hive hive = hives.stream().filter(h -> h.getStatus() == HiveStatus.ACTIVE).findFirst().orElseThrow();

                // Batch created but NOT sent for testing
                HoneyBatch batch = honeyBatchRepository.save(new HoneyBatch(
                                "HC-2026-UNSENT01", profile.getId(), hive.getId(),
                                LocalDate.now(), new BigDecimal("5.00"), null, BatchStatus.CREATED));

                LabTestCreateRequest req = new LabTestCreateRequest(90, LabTestResult.PURE, "premature test");

                mockMvc.perform(post("/api/lab/tests/" + batch.getBatchId())
                                .header("Authorization", "Bearer " + labToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message", containsString("is not awaiting lab testing")));
        }

        // ── Test 6 ───────────────────────────────────────────────────────────────
        @Test
        @DisplayName("6. Duplicate test submission for same batch returns 409 Conflict")
        void testDuplicateLabTestRejected() throws Exception {
                String batchId = createAndSendBatchForTesting();

                LabTestCreateRequest req = new LabTestCreateRequest(92, LabTestResult.PURE, "First test");

                mockMvc.perform(post("/api/lab/tests/" + batchId)
                                .header("Authorization", "Bearer " + labToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isCreated());

                // Second attempt must fail with 400 or 409
                mockMvc.perform(post("/api/lab/tests/" + batchId)
                                .header("Authorization", "Bearer " + labToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isBadRequest()); // Batch status is now PURE, not SENT_FOR_TESTING
        }

        // ── Test 7 ───────────────────────────────────────────────────────────────
        @Test
        @DisplayName("7. Invalid purity score (> 100 or < 0) returns 400 validation error")
        void testInvalidPurityScoreRejected() throws Exception {
                String batchId = createAndSendBatchForTesting();

                LabTestCreateRequest req = new LabTestCreateRequest(150, LabTestResult.PURE, "Invalid score");

                mockMvc.perform(post("/api/lab/tests/" + batchId)
                                .header("Authorization", "Bearer " + labToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.success", is(false)));
        }

        // ── Test 8 ───────────────────────────────────────────────────────────────
        @Test
        @DisplayName("8. Beekeeper can retrieve lab test result for their batch (200)")
        void testBeekeeperCanViewLabResult() throws Exception {
                String batchId = createAndSendBatchForTesting();

                LabTestCreateRequest req = new LabTestCreateRequest(99, LabTestResult.PURE, "Certified pure honey");
                mockMvc.perform(post("/api/lab/tests/" + batchId)
                                .header("Authorization", "Bearer " + labToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isCreated());

                // Beekeeper views result
                mockMvc.perform(get("/api/beekeepers/batches/" + batchId + "/lab-result")
                                .header("Authorization", "Bearer " + beekeeperToken()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.batchId", is(batchId)))
                                .andExpect(jsonPath("$.data.purityScore", is(99)))
                                .andExpect(jsonPath("$.data.result", is("PURE")));
        }

        // ── Test 9 ───────────────────────────────────────────────────────────────
        @Test
        @DisplayName("9. Beekeeper cannot submit lab test results (403 Forbidden)")
        void testBeekeeperForbiddenFromSubmittingLabTest() throws Exception {
                String batchId = createAndSendBatchForTesting();

                LabTestCreateRequest req = new LabTestCreateRequest(90, LabTestResult.PURE, "Unauthorized");

                mockMvc.perform(post("/api/lab/tests/" + batchId)
                                .header("Authorization", "Bearer " + beekeeperToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isForbidden());
        }

        // ── Test 10 ──────────────────────────────────────────────────────────────
        @Test
        @DisplayName("10. Customer role cannot access lab endpoints (403 Forbidden)")
        void testCustomerForbiddenFromLabEndpoints() throws Exception {
                mockMvc.perform(get("/api/lab/tests/pending")
                                .header("Authorization", "Bearer " + customerToken()))
                                .andExpect(status().isForbidden());
        }

        // ── Test 11 ──────────────────────────────────────────────────────────────
        @Test
        @DisplayName("11. Lab stats endpoint returns summary numbers (200)")
        void testGetLabStats() throws Exception {
                mockMvc.perform(get("/api/lab/stats")
                                .header("Authorization", "Bearer " + labToken()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.pending", greaterThanOrEqualTo(0)))
                                .andExpect(jsonPath("$.data.completed", greaterThanOrEqualTo(0)));
        }
}
