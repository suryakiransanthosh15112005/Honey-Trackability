package com.honeychain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeychain.batch.dto.HoneyBatchCreateRequest;
import com.honeychain.batch.dto.HoneyBatchUpdateRequest;
import com.honeychain.batch.entity.BatchStatus;
import com.honeychain.beekeeper.entity.BeekeeperProfile;
import com.honeychain.beekeeper.entity.BeekeeperVerificationStatus;
import com.honeychain.beekeeper.entity.PreferredLanguage;
import com.honeychain.beekeeper.repository.BeekeeperProfileRepository;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class HoneyBatchIntegrationTest {

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

        private final String BEEKEEPER_PHONE = "9876543213";

        private String beekeeperToken() {
                return jwtService.generateToken(BEEKEEPER_PHONE, "BEEKEEPER");
        }

        private String customerToken() {
                return jwtService.generateToken("9876543214", "CUSTOMER");
        }

        private String labToken() {
                return jwtService.generateToken("9876543212", "LAB");
        }

        private Hive getActiveHiveForBeekeeper() {
                User user = userRepository.findByPhoneNumber(BEEKEEPER_PHONE).orElseThrow();
                BeekeeperProfile profile = beekeeperProfileRepository.findByUserId(user.getId()).orElseThrow();
                List<Hive> hives = hiveRepository.findAllByBeekeeperProfileId(profile.getId());
                return hives.stream()
                                .filter(h -> h.getStatus() == HiveStatus.ACTIVE)
                                .findFirst()
                                .orElseGet(() -> hiveRepository.save(new Hive(
                                                profile.getId(), "HIVE-AUTO-01", "Cluster Alpha",
                                                11.42, 76.88, HiveStatus.ACTIVE, LocalDate.now().minusDays(30))));
        }

        // ── Test 1 ───────────────────────────────────────────────────────────────
        @Test
        @DisplayName("1. Authenticated beekeeper can create batch with JSON (201 Created)")
        void testCreateBatchJsonSuccess() throws Exception {
                Hive hive = getActiveHiveForBeekeeper();
                HoneyBatchCreateRequest req = new HoneyBatchCreateRequest(
                                hive.getId(), LocalDate.now(), new BigDecimal("15.50"));

                mockMvc.perform(post("/api/beekeepers/batches")
                                .header("Authorization", "Bearer " + beekeeperToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.data.batchId", startsWith("HC-")))
                                .andExpect(jsonPath("$.data.quantityKg", is(15.50)))
                                .andExpect(jsonPath("$.data.status", is("CREATED")))
                                .andExpect(jsonPath("$.data.hiveCode", notNullValue()));
        }

        // ── Test 2 ───────────────────────────────────────────────────────────────
        @Test
        @DisplayName("2. Authenticated beekeeper can create batch with Multipart and photo (201 Created)")
        void testCreateBatchMultipartSuccess() throws Exception {
                Hive hive = getActiveHiveForBeekeeper();

                MockMultipartFile photo = new MockMultipartFile(
                                "photo", "batch.jpg", "image/jpeg", "sample photo content".getBytes());

                mockMvc.perform(multipart("/api/beekeepers/batches")
                                .file(photo)
                                .param("hiveId", hive.getId().toString())
                                .param("harvestDate", LocalDate.now().toString())
                                .param("quantityKg", "22.75")
                                .header("Authorization", "Bearer " + beekeeperToken()))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.data.batchId", startsWith("HC-")))
                                .andExpect(jsonPath("$.data.quantityKg", is(22.75)))
                                .andExpect(jsonPath("$.data.photoUrl", startsWith("/uploads/batches/")));
        }

        // ── Test 3 ───────────────────────────────────────────────────────────────
        @Test
        @DisplayName("3. Unauthenticated request returns 401 Unauthorized")
        void testUnauthenticatedReturns401() throws Exception {
                mockMvc.perform(get("/api/beekeepers/batches"))
                                .andExpect(status().isUnauthorized());
        }

        // ── Test 4 ───────────────────────────────────────────────────────────────
        @Test
        @DisplayName("4. Customer role accessing batch endpoint returns 403 Forbidden")
        void testCustomerForbidden() throws Exception {
                mockMvc.perform(get("/api/beekeepers/batches")
                                .header("Authorization", "Bearer " + customerToken()))
                                .andExpect(status().isForbidden());
        }

        // ── Test 5 ───────────────────────────────────────────────────────────────
        @Test
        @DisplayName("5. Lab role accessing beekeeper batch endpoint returns 403 Forbidden")
        void testLabForbidden() throws Exception {
                mockMvc.perform(get("/api/beekeepers/batches")
                                .header("Authorization", "Bearer " + labToken()))
                                .andExpect(status().isForbidden());
        }

        // ── Test 6 ───────────────────────────────────────────────────────────────
        @Test
        @DisplayName("6. Create batch with future harvest date returns 400 Bad Request")
        void testFutureHarvestDateReturns400() throws Exception {
                Hive hive = getActiveHiveForBeekeeper();
                HoneyBatchCreateRequest req = new HoneyBatchCreateRequest(
                                hive.getId(), LocalDate.now().plusDays(5), new BigDecimal("10.00"));

                mockMvc.perform(post("/api/beekeepers/batches")
                                .header("Authorization", "Bearer " + beekeeperToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.success", is(false)));
        }

        // ── Test 7 ───────────────────────────────────────────────────────────────
        @Test
        @DisplayName("7. Create batch with negative or zero quantity returns 400 Bad Request")
        void testInvalidQuantityReturns400() throws Exception {
                Hive hive = getActiveHiveForBeekeeper();
                HoneyBatchCreateRequest req = new HoneyBatchCreateRequest(
                                hive.getId(), LocalDate.now(), new BigDecimal("0.00"));

                mockMvc.perform(post("/api/beekeepers/batches")
                                .header("Authorization", "Bearer " + beekeeperToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.success", is(false)));
        }

        // ── Test 8 ───────────────────────────────────────────────────────────────
        @Test
        @DisplayName("8. Create batch using non-existent hive returns 400 Bad Request")
        void testNonExistentHiveReturns400() throws Exception {
                HoneyBatchCreateRequest req = new HoneyBatchCreateRequest(
                                999999L, LocalDate.now(), new BigDecimal("10.00"));

                mockMvc.perform(post("/api/beekeepers/batches")
                                .header("Authorization", "Bearer " + beekeeperToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.success", is(false)));
        }

        // ── Test 9 ───────────────────────────────────────────────────────────────
        @Test
        @DisplayName("9. Create batch using inactive hive returns 400 Bad Request")
        void testInactiveHiveReturns400() throws Exception {
                User user = userRepository.findByPhoneNumber(BEEKEEPER_PHONE).orElseThrow();
                BeekeeperProfile profile = beekeeperProfileRepository.findByUserId(user.getId()).orElseThrow();

                Hive inactiveHive = hiveRepository.save(new Hive(
                                profile.getId(), "HIVE-INACT-01", "Inactive Cluster",
                                11.0, 76.0, HiveStatus.INACTIVE, LocalDate.now().minusDays(10)));

                HoneyBatchCreateRequest req = new HoneyBatchCreateRequest(
                                inactiveHive.getId(), LocalDate.now(), new BigDecimal("10.00"));

                mockMvc.perform(post("/api/beekeepers/batches")
                                .header("Authorization", "Bearer " + beekeeperToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message",
                                                containsString("Hive is not currently available for harvesting")));
        }

        // ── Test 10 ──────────────────────────────────────────────────────────────
        @Test
        @DisplayName("10. Beekeeper cannot create batch using another beekeeper's hive (400)")
        void testAnotherBeekeepersHiveReturns400() throws Exception {
                // Create second beekeeper + profile + hive
                String otherPhone = "9876540099";
                User otherUser = userRepository.findByPhoneNumber(otherPhone).orElseGet(
                                () -> userRepository.save(new User(otherPhone, "pass", Role.BEEKEEPER, true)));

                BeekeeperProfile otherProfile = beekeeperProfileRepository.findByUserId(otherUser.getId())
                                .orElseGet(() -> beekeeperProfileRepository.save(new BeekeeperProfile(
                                                otherUser.getId(), "KVIC-OTHER-01", "Other Beekeeper", "Village", null,
                                                10.0, 70.0, PreferredLanguage.ENGLISH,
                                                BeekeeperVerificationStatus.APPROVED)));

                Hive otherHive = hiveRepository.save(new Hive(
                                otherProfile.getId(), "HIVE-OTHER-01", "Other Cluster",
                                10.0, 70.0, HiveStatus.ACTIVE, LocalDate.now().minusDays(5)));

                // Try to create batch with main beekeeper token against other's hive
                HoneyBatchCreateRequest req = new HoneyBatchCreateRequest(
                                otherHive.getId(), LocalDate.now(), new BigDecimal("10.00"));

                mockMvc.perform(post("/api/beekeepers/batches")
                                .header("Authorization", "Bearer " + beekeeperToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message", containsString("does not belong to your account")));
        }

        // ── Test 11 ──────────────────────────────────────────────────────────────
        @Test
        @DisplayName("11. Get my batches returns paginated PageResponse (200)")
        void testGetMyBatchesPagination() throws Exception {
                mockMvc.perform(get("/api/beekeepers/batches?page=0&size=5")
                                .header("Authorization", "Bearer " + beekeeperToken()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.data.content", isA(List.class)))
                                .andExpect(jsonPath("$.data.pageSize", is(5)));
        }

        // ── Test 12 ──────────────────────────────────────────────────────────────
        @Test
        @DisplayName("12. Update batch in CREATED status modifies quantity and date (200)")
        void testUpdateBatchSuccess() throws Exception {
                Hive hive = getActiveHiveForBeekeeper();
                HoneyBatchCreateRequest req = new HoneyBatchCreateRequest(
                                hive.getId(), LocalDate.now(), new BigDecimal("10.00"));

                MvcResult createRes = mockMvc.perform(post("/api/beekeepers/batches")
                                .header("Authorization", "Bearer " + beekeeperToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andReturn();

                String batchId = objectMapper.readTree(createRes.getResponse().getContentAsString())
                                .at("/data/batchId").asText();

                HoneyBatchUpdateRequest updateReq = new HoneyBatchUpdateRequest(
                                LocalDate.now().minusDays(1), new BigDecimal("18.25"));

                mockMvc.perform(put("/api/beekeepers/batches/" + batchId)
                                .header("Authorization", "Bearer " + beekeeperToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateReq)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.quantityKg", is(18.25)))
                                .andExpect(jsonPath("$.data.batchId", is(batchId)));
        }

        // ── Test 13 ──────────────────────────────────────────────────────────────
        @Test
        @DisplayName("13. Send for testing transitions CREATED -> SENT_FOR_TESTING (200)")
        void testSendForTestingSuccess() throws Exception {
                Hive hive = getActiveHiveForBeekeeper();
                HoneyBatchCreateRequest req = new HoneyBatchCreateRequest(
                                hive.getId(), LocalDate.now(), new BigDecimal("10.00"));

                MvcResult createRes = mockMvc.perform(post("/api/beekeepers/batches")
                                .header("Authorization", "Bearer " + beekeeperToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andReturn();

                String batchId = objectMapper.readTree(createRes.getResponse().getContentAsString())
                                .at("/data/batchId").asText();

                // Send for testing
                mockMvc.perform(post("/api/beekeepers/batches/" + batchId + "/send-testing")
                                .header("Authorization", "Bearer " + beekeeperToken()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.status", is("SENT_FOR_TESTING")));

                // Attempting to edit after SENT_FOR_TESTING must be rejected (400)
                HoneyBatchUpdateRequest updateReq = new HoneyBatchUpdateRequest(
                                LocalDate.now(), new BigDecimal("12.00"));

                mockMvc.perform(put("/api/beekeepers/batches/" + batchId)
                                .header("Authorization", "Bearer " + beekeeperToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateReq)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message",
                                                containsString("Only batches in CREATED status can be modified")));

                // Attempting to send for testing again must be rejected (400)
                mockMvc.perform(post("/api/beekeepers/batches/" + batchId + "/send-testing")
                                .header("Authorization", "Bearer " + beekeeperToken()))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message", containsString(
                                                "Only batches in CREATED status can be sent for testing")));
        }

        // ── Test 14 ──────────────────────────────────────────────────────────────
        @Test
        @DisplayName("14. Batch stats endpoint returns statistics counts (200)")
        void testGetBatchStats() throws Exception {
                mockMvc.perform(get("/api/beekeepers/batches/stats")
                                .header("Authorization", "Bearer " + beekeeperToken()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.total", greaterThanOrEqualTo(0)))
                                .andExpect(jsonPath("$.data.created", greaterThanOrEqualTo(0)))
                                .andExpect(jsonPath("$.data.sentForTesting", greaterThanOrEqualTo(0)));
        }
}
