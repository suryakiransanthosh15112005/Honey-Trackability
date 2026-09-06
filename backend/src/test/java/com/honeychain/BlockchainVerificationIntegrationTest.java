package com.honeychain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeychain.batch.dto.HoneyBatchCreateRequest;
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
import com.honeychain.security.jwt.JwtService;
import com.honeychain.user.entity.Role;
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
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BlockchainVerificationIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private JwtService jwtService;
    @Autowired private UserRepository userRepository;
    @Autowired private BeekeeperProfileRepository beekeeperProfileRepository;
    @Autowired private HiveRepository hiveRepository;
    @Autowired private HoneyBatchRepository honeyBatchRepository;
    @Autowired private BlockchainRecordRepository blockchainRecordRepository;

    private final String BEEKEEPER_PHONE = "9876543213";

    private String beekeeperToken() {
        return jwtService.generateToken(BEEKEEPER_PHONE, "BEEKEEPER");
    }

    private String customerToken() {
        return jwtService.generateToken("9876543214", "CUSTOMER");
    }

    private Hive getActiveHiveForBeekeeper() {
        User user = userRepository.findByPhoneNumber(BEEKEEPER_PHONE).orElseThrow();
        BeekeeperProfile profile = beekeeperProfileRepository.findByUserId(user.getId()).orElseThrow();
        List<Hive> hives = hiveRepository.findAllByBeekeeperProfileId(profile.getId());
        return hives.stream()
                .filter(h -> h.getStatus() == HiveStatus.ACTIVE)
                .findFirst()
                .orElseGet(() -> hiveRepository.save(new Hive(
                        profile.getId(), "HIVE-AUTO-BC", "Cluster BC",
                        11.42, 76.88, HiveStatus.ACTIVE, LocalDate.now().minusDays(20)
                )));
    }

    // ── Test 1 ───────────────────────────────────────────────────────────────
    @Test
    @DisplayName("1. Batch creation automatically records immutable hash on blockchain")
    void testAutomaticBlockchainRecordingOnBatchCreation() throws Exception {
        Hive hive = getActiveHiveForBeekeeper();
        HoneyBatchCreateRequest req = new HoneyBatchCreateRequest(
                hive.getId(), LocalDate.now(), new BigDecimal("14.50"));

        MvcResult result = mockMvc.perform(post("/api/beekeepers/batches")
                        .header("Authorization", "Bearer " + beekeeperToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();

        String batchId = objectMapper.readTree(result.getResponse().getContentAsString())
                .at("/data/batchId").asText();

        // Verify BlockchainRecord exists in database
        Optional<BlockchainRecord> recordOpt = blockchainRecordRepository.findByBatchIdAndRecordType(
                batchId, BlockchainRecordType.BATCH_CREATED);

        assertTrue(recordOpt.isPresent(), "Blockchain record must be created automatically on batch creation");
        BlockchainRecord record = recordOpt.get();
        assertEquals(batchId, record.getBatchId());
        assertNotNull(record.getDataHash());
        assertEquals(64, record.getDataHash().length());
        assertTrue(record.getTransactionHash().startsWith("0x"));
        assertEquals("HONEYCHAIN-MOCKNET", record.getNetwork());
        assertTrue(record.getBlockNumber() > 100000L);
    }

    // ── Test 2 ───────────────────────────────────────────────────────────────
    @Test
    @DisplayName("2. Get blockchain record endpoint returns stored blockchain details (200)")
    void testGetBlockchainRecordEndpoint() throws Exception {
        Hive hive = getActiveHiveForBeekeeper();
        HoneyBatchCreateRequest req = new HoneyBatchCreateRequest(
                hive.getId(), LocalDate.now(), new BigDecimal("9.25"));

        MvcResult result = mockMvc.perform(post("/api/beekeepers/batches")
                        .header("Authorization", "Bearer " + beekeeperToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();

        String batchId = objectMapper.readTree(result.getResponse().getContentAsString())
                .at("/data/batchId").asText();

        // Query blockchain record
        mockMvc.perform(get("/api/beekeepers/batches/" + batchId + "/blockchain")
                        .header("Authorization", "Bearer " + beekeeperToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.recorded", is(true)))
                .andExpect(jsonPath("$.data.batchId", is(batchId)))
                .andExpect(jsonPath("$.data.network", is("HONEYCHAIN-MOCKNET")))
                .andExpect(jsonPath("$.data.transactionHash", startsWith("0x")))
                .andExpect(jsonPath("$.data.dataHash", notNullValue()));
    }

    // ── Test 3 ───────────────────────────────────────────────────────────────
    @Test
    @DisplayName("3. Verify batch returns verified: true when batch data is unmodified")
    void testVerifyBatchSuccess() throws Exception {
        Hive hive = getActiveHiveForBeekeeper();
        HoneyBatchCreateRequest req = new HoneyBatchCreateRequest(
                hive.getId(), LocalDate.now(), new BigDecimal("18.00"));

        MvcResult result = mockMvc.perform(post("/api/beekeepers/batches")
                        .header("Authorization", "Bearer " + beekeeperToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();

        String batchId = objectMapper.readTree(result.getResponse().getContentAsString())
                .at("/data/batchId").asText();

        // Perform verification
        mockMvc.perform(post("/api/beekeepers/batches/" + batchId + "/blockchain/verify")
                        .header("Authorization", "Bearer " + beekeeperToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.verified", is(true)))
                .andExpect(jsonPath("$.data.batchId", is(batchId)))
                .andExpect(jsonPath("$.data.storedHash", notNullValue()))
                .andExpect(jsonPath("$.data.calculatedHash", notNullValue()))
                .andExpect(jsonPath("$.data.transactionHash", startsWith("0x")))
                .andExpect(jsonPath("$.data.blockNumber", greaterThan(0)));
    }

    // ── Test 4 (CRITICAL TAMPER TEST) ─────────────────────────────────────────
    @Test
    @DisplayName("4. TAMPER TEST: Tampered database data fails verification (verified: false)")
    void testTamperedDataFailsVerification() throws Exception {
        // 1. Create a genuine batch
        Hive hive = getActiveHiveForBeekeeper();
        HoneyBatchCreateRequest req = new HoneyBatchCreateRequest(
                hive.getId(), LocalDate.now(), new BigDecimal("8.50"));

        MvcResult result = mockMvc.perform(post("/api/beekeepers/batches")
                        .header("Authorization", "Bearer " + beekeeperToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();

        String batchId = objectMapper.readTree(result.getResponse().getContentAsString())
                .at("/data/batchId").asText();

        // 2. Verify it starts as verified
        mockMvc.perform(post("/api/beekeepers/batches/" + batchId + "/blockchain/verify")
                        .header("Authorization", "Bearer " + beekeeperToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.verified", is(true)));

        // 3. TAMPER: Directly modify batch quantity in database without touching blockchain record
        HoneyBatch batchInDb = honeyBatchRepository.findByBatchId(batchId).orElseThrow();
        batchInDb.setQuantityKg(new BigDecimal("9.50")); // altered from 8.50 to 9.50
        honeyBatchRepository.save(batchInDb);

        // 4. Verification must now detect the tamper and return verified = false
        mockMvc.perform(post("/api/beekeepers/batches/" + batchId + "/blockchain/verify")
                        .header("Authorization", "Bearer " + beekeeperToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.verified", is(false)))
                .andExpect(jsonPath("$.data.batchId", is(batchId)))
                .andExpect(jsonPath("$.data.message", containsString("modified and does not match")));
    }

    // ── Test 5 ───────────────────────────────────────────────────────────────
    @Test
    @DisplayName("5. Unauthenticated request to blockchain endpoints returns 401")
    void testUnauthenticatedAccessReturns401() throws Exception {
        mockMvc.perform(get("/api/beekeepers/batches/HC-2026-AB12CD34/blockchain"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/beekeepers/batches/HC-2026-AB12CD34/blockchain/verify"))
                .andExpect(status().isUnauthorized());
    }

    // ── Test 6 ───────────────────────────────────────────────────────────────
    @Test
    @DisplayName("6. Customer role accessing private blockchain endpoint returns 403")
    void testCustomerForbidden() throws Exception {
        mockMvc.perform(get("/api/beekeepers/batches/HC-2026-AB12CD34/blockchain")
                        .header("Authorization", "Bearer " + customerToken()))
                .andExpect(status().isForbidden());
    }
}
