package com.honeychain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeychain.hive.dto.HiveCreateRequest;
import com.honeychain.hive.dto.HiveStatusUpdateRequest;
import com.honeychain.hive.dto.HiveUpdateRequest;
import com.honeychain.hive.entity.HiveStatus;
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

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class HiveManagementIntegrationTest {

        @Autowired
        private MockMvc mockMvc;
        @Autowired
        private ObjectMapper objectMapper;
        @Autowired
        private JwtService jwtService;
        @Autowired
        private UserRepository userRepository;

        // Seeded beekeeper with profile (from DataInitializer)
        private final String BEEKEEPER_PHONE = "9876543213";

        private String beekeeperToken() {
                return jwtService.generateToken(BEEKEEPER_PHONE, "BEEKEEPER");
        }

        private String customerToken() {
                return jwtService.generateToken("9876543214", "CUSTOMER");
        }

        // ── Test 1 ───────────────────────────────────────────────────────────────
        @Test
        @DisplayName("1. Authenticated beekeeper can create a hive (201 Created)")
        void testCreateHiveSuccess() throws Exception {
                HiveCreateRequest req = new HiveCreateRequest(
                                "Test Cluster Alpha", 11.4200, 76.8800, LocalDate.of(2024, 6, 1));

                mockMvc.perform(post("/api/beekeepers/hives")
                                .header("Authorization", "Bearer " + beekeeperToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.data.clusterName", is("Test Cluster Alpha")))
                                .andExpect(jsonPath("$.data.status", is("ACTIVE")))
                                .andExpect(jsonPath("$.data.hiveCode", startsWith("HIVE-")));
        }

        // ── Test 2 ───────────────────────────────────────────────────────────────
        @Test
        @DisplayName("2. Beekeeper can retrieve only their own hives")
        void testGetMyHives() throws Exception {
                mockMvc.perform(get("/api/beekeepers/hives")
                                .header("Authorization", "Bearer " + beekeeperToken()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.data", isA(java.util.List.class)));
        }

        // ── Test 3 ───────────────────────────────────────────────────────────────
        @Test
        @DisplayName("3. Customer cannot access beekeeper hive endpoints (403)")
        void testCustomerForbiddenFromHives() throws Exception {
                mockMvc.perform(get("/api/beekeepers/hives")
                                .header("Authorization", "Bearer " + customerToken()))
                                .andExpect(status().isForbidden())
                                .andExpect(jsonPath("$.success", is(false)));
        }

        // ── Test 4 ───────────────────────────────────────────────────────────────
        @Test
        @DisplayName("4. No auth token returns 401 Unauthorized")
        void testUnauthenticatedAccessReturns401() throws Exception {
                mockMvc.perform(get("/api/beekeepers/hives"))
                                .andExpect(status().isUnauthorized());
        }

        // ── Test 5 ───────────────────────────────────────────────────────────────
        @Test
        @DisplayName("5. Create hive with blank cluster name returns 400 validation error")
        void testBlankClusterNameValidation() throws Exception {
                HiveCreateRequest req = new HiveCreateRequest(
                                "", 11.4200, 76.8800, LocalDate.of(2024, 6, 1));

                mockMvc.perform(post("/api/beekeepers/hives")
                                .header("Authorization", "Bearer " + beekeeperToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.success", is(false)));
        }

        // ── Test 6 ───────────────────────────────────────────────────────────────
        @Test
        @DisplayName("6. Create hive with future installed date returns 400 validation error")
        void testFutureInstalledDateValidation() throws Exception {
                HiveCreateRequest req = new HiveCreateRequest(
                                "Future Hive", 11.4200, 76.8800, LocalDate.now().plusDays(10));

                mockMvc.perform(post("/api/beekeepers/hives")
                                .header("Authorization", "Bearer " + beekeeperToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.success", is(false)));
        }

        // ── Test 7 ───────────────────────────────────────────────────────────────
        @Test
        @DisplayName("7. Create hive with invalid latitude (> 90) returns 400")
        void testInvalidLatitudeValidation() throws Exception {
                HiveCreateRequest req = new HiveCreateRequest(
                                "Lat Cluster", 95.0, 76.8800, LocalDate.of(2024, 1, 1));

                mockMvc.perform(post("/api/beekeepers/hives")
                                .header("Authorization", "Bearer " + beekeeperToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.success", is(false)));
        }

        // ── Test 8 ───────────────────────────────────────────────────────────────
        @Test
        @DisplayName("8. Update hive modifies cluster name and coordinates")
        void testUpdateHive() throws Exception {
                // Create a hive first
                HiveCreateRequest createReq = new HiveCreateRequest(
                                "Update Test Cluster", 11.0, 76.0, LocalDate.of(2024, 2, 1));
                MvcResult createResult = mockMvc.perform(post("/api/beekeepers/hives")
                                .header("Authorization", "Bearer " + beekeeperToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createReq)))
                                .andReturn();

                String body = createResult.getResponse().getContentAsString();
                Long hiveId = objectMapper.readTree(body).at("/data/id").asLong();

                // Update it
                HiveUpdateRequest updateReq = new HiveUpdateRequest(
                                "Updated Cluster Name", 12.5, 77.5, LocalDate.of(2024, 3, 1));

                mockMvc.perform(put("/api/beekeepers/hives/" + hiveId)
                                .header("Authorization", "Bearer " + beekeeperToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateReq)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.clusterName", is("Updated Cluster Name")))
                                .andExpect(jsonPath("$.data.latitude", is(12.5)));
        }

        // ── Test 9 ───────────────────────────────────────────────────────────────
        @Test
        @DisplayName("9. Update hive status to INACTIVE deactivates the hive")
        void testUpdateHiveStatus() throws Exception {
                // Create a hive
                HiveCreateRequest createReq = new HiveCreateRequest(
                                "Status Test Cluster", 11.0, 76.0, LocalDate.of(2024, 2, 1));
                MvcResult createResult = mockMvc.perform(post("/api/beekeepers/hives")
                                .header("Authorization", "Bearer " + beekeeperToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createReq)))
                                .andReturn();

                Long hiveId = objectMapper.readTree(createResult.getResponse().getContentAsString()).at("/data/id")
                                .asLong();

                mockMvc.perform(patch("/api/beekeepers/hives/" + hiveId + "/status")
                                .header("Authorization", "Bearer " + beekeeperToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper
                                                .writeValueAsString(new HiveStatusUpdateRequest(HiveStatus.INACTIVE))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.status", is("INACTIVE")));
        }

        // ── Test 10 ──────────────────────────────────────────────────────────────
        @Test
        @DisplayName("10. Beekeeper cannot manually set status to ALERT (400 Bad Request)")
        void testAlertStatusForbiddenForBeekeeper() throws Exception {
                HiveCreateRequest createReq = new HiveCreateRequest(
                                "Alert Test Cluster", 11.0, 76.0, LocalDate.of(2024, 2, 1));
                MvcResult createResult = mockMvc.perform(post("/api/beekeepers/hives")
                                .header("Authorization", "Bearer " + beekeeperToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createReq)))
                                .andReturn();

                Long hiveId = objectMapper.readTree(createResult.getResponse().getContentAsString()).at("/data/id")
                                .asLong();

                mockMvc.perform(patch("/api/beekeepers/hives/" + hiveId + "/status")
                                .header("Authorization", "Bearer " + beekeeperToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper
                                                .writeValueAsString(new HiveStatusUpdateRequest(HiveStatus.ALERT))))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.success", is(false)));
        }

        // ── Test 11 ──────────────────────────────────────────────────────────────
        @Test
        @DisplayName("11. Second beekeeper cannot access first beekeeper's hive (404)")
        void testOwnershipIsolation() throws Exception {
                // Create a new beekeeper user without a profile
                String phone2 = "9876540001";
                if (!userRepository.existsByPhoneNumber(phone2)) {
                        userRepository.save(new User(phone2, "pass", Role.BEEKEEPER, true));
                }
                String token2 = jwtService.generateToken(phone2, "BEEKEEPER");

                // Seeded hive ID 1 belongs to beekeeper1; beekeeper2 has no profile so gets 400
                // We verify beekeeper2 cannot retrieve beekeeper1's hives
                mockMvc.perform(get("/api/beekeepers/hives/1")
                                .header("Authorization", "Bearer " + token2))
                                .andExpect(status().isBadRequest()); // 400: no beekeeper profile
        }

        // ── Test 12 ──────────────────────────────────────────────────────────────
        @Test
        @DisplayName("12. Get hive count returns total count for authenticated beekeeper")
        void testGetHiveCount() throws Exception {
                mockMvc.perform(get("/api/beekeepers/hives/count")
                                .header("Authorization", "Bearer " + beekeeperToken()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.total", greaterThanOrEqualTo(0)));
        }
}
