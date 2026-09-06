package com.honeychain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeychain.beekeeper.dto.BeekeeperProfileRequest;
import com.honeychain.beekeeper.dto.BeekeeperProfileUpdateRequest;
import com.honeychain.beekeeper.entity.PreferredLanguage;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BeekeeperProfileIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private JwtService jwtService;

        @Autowired
        private UserRepository userRepository;

        private String testBeekeeperPhone = "9876543299";
        private String testBeekeeperToken;

        @BeforeEach
        void setUp() {
                if (!userRepository.existsByPhoneNumber(testBeekeeperPhone)) {
                        User user = new User(testBeekeeperPhone, "password", Role.BEEKEEPER, true);
                        userRepository.save(user);
                }
                testBeekeeperToken = jwtService.generateToken(testBeekeeperPhone, "BEEKEEPER");
        }

        @Test
        @DisplayName("1. Create beekeeper profile successfully (201 Created)")
        void testCreateProfileSuccess() throws Exception {
                String uniquePhone = "9876543288";
                if (!userRepository.existsByPhoneNumber(uniquePhone)) {
                        userRepository.save(new User(uniquePhone, "pass", Role.BEEKEEPER, true));
                }
                String token = jwtService.generateToken(uniquePhone, "BEEKEEPER");

                BeekeeperProfileRequest request = new BeekeeperProfileRequest(
                                "KVIC-TEST-001",
                                "Arun Prakash",
                                "Coonoor",
                                "https://example.com/photo.jpg",
                                11.3530,
                                76.7959,
                                PreferredLanguage.TAMIL);

                mockMvc.perform(post("/api/beekeepers/profile")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.data.kvicId", is("KVIC-TEST-001")))
                                .andExpect(jsonPath("$.data.name", is("Arun Prakash")))
                                .andExpect(jsonPath("$.data.village", is("Coonoor")))
                                .andExpect(jsonPath("$.data.verificationStatus", is("PENDING")));
        }

        @Test
        @DisplayName("2. Create profile without authentication returns 401 Unauthorized")
        void testCreateProfileUnauthorized() throws Exception {
                BeekeeperProfileRequest request = new BeekeeperProfileRequest(
                                "KVIC-TEST-002",
                                "Unauthorized User",
                                "Village",
                                null,
                                11.0,
                                76.0,
                                PreferredLanguage.ENGLISH);

                mockMvc.perform(post("/api/beekeepers/profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("3. Duplicate profile creation for same user returns 409 Conflict")
        void testDuplicateProfileForSameUser() throws Exception {
                String userPhone = "9876543277";
                if (!userRepository.existsByPhoneNumber(userPhone)) {
                        userRepository.save(new User(userPhone, "pass", Role.BEEKEEPER, true));
                }
                String token = jwtService.generateToken(userPhone, "BEEKEEPER");

                BeekeeperProfileRequest req1 = new BeekeeperProfileRequest(
                                "KVIC-DUP-01", "User A", "Village A", null, 11.0, 76.0, PreferredLanguage.ENGLISH);
                mockMvc.perform(post("/api/beekeepers/profile")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req1)))
                                .andExpect(status().isCreated());

                // Attempt second profile for same user
                BeekeeperProfileRequest req2 = new BeekeeperProfileRequest(
                                "KVIC-DUP-02", "User A", "Village A", null, 11.0, 76.0, PreferredLanguage.ENGLISH);
                mockMvc.perform(post("/api/beekeepers/profile")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req2)))
                                .andExpect(status().isConflict())
                                .andExpect(jsonPath("$.success", is(false)));
        }

        @Test
        @DisplayName("4. Duplicate KVIC ID from another user returns 409 Conflict")
        void testDuplicateKvicId() throws Exception {
                // User 1 creates KVIC-UNIQUE-KVIC
                String phone1 = "9876543266";
                if (!userRepository.existsByPhoneNumber(phone1)) {
                        userRepository.save(new User(phone1, "pass", Role.BEEKEEPER, true));
                }
                String token1 = jwtService.generateToken(phone1, "BEEKEEPER");

                mockMvc.perform(post("/api/beekeepers/profile")
                                .header("Authorization", "Bearer " + token1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new BeekeeperProfileRequest(
                                                "KVIC-SHARED-01", "User 1", "Village 1", null, 11.0, 76.0,
                                                PreferredLanguage.HINDI))));

                // User 2 tries to reuse KVIC-SHARED-01
                String phone2 = "9876543255";
                if (!userRepository.existsByPhoneNumber(phone2)) {
                        userRepository.save(new User(phone2, "pass", Role.BEEKEEPER, true));
                }
                String token2 = jwtService.generateToken(phone2, "BEEKEEPER");

                mockMvc.perform(post("/api/beekeepers/profile")
                                .header("Authorization", "Bearer " + token2)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new BeekeeperProfileRequest(
                                                "KVIC-SHARED-01", "User 2", "Village 2", null, 11.0, 76.0,
                                                PreferredLanguage.TAMIL))))
                                .andExpect(status().isConflict())
                                .andExpect(jsonPath("$.success", is(false)));
        }

        @Test
        @DisplayName("5. Invalid blank name returns 400 Bad Request validation error")
        void testInvalidNameValidation() throws Exception {
                BeekeeperProfileRequest request = new BeekeeperProfileRequest(
                                "KVIC-VAL-01",
                                "", // Blank name
                                "Village",
                                null,
                                11.0,
                                76.0,
                                PreferredLanguage.ENGLISH);

                mockMvc.perform(post("/api/beekeepers/profile")
                                .header("Authorization", "Bearer " + testBeekeeperToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.success", is(false)));
        }

        @Test
        @DisplayName("6. Invalid latitude > 90 returns 400 Bad Request validation error")
        void testInvalidLatitudeValidation() throws Exception {
                BeekeeperProfileRequest request = new BeekeeperProfileRequest(
                                "KVIC-VAL-02",
                                "Valid Name",
                                "Village",
                                null,
                                95.0, // Invalid latitude
                                76.0,
                                PreferredLanguage.ENGLISH);

                mockMvc.perform(post("/api/beekeepers/profile")
                                .header("Authorization", "Bearer " + testBeekeeperToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.success", is(false)));
        }

        @Test
        @DisplayName("7. Invalid longitude > 180 returns 400 Bad Request validation error")
        void testInvalidLongitudeValidation() throws Exception {
                BeekeeperProfileRequest request = new BeekeeperProfileRequest(
                                "KVIC-VAL-03",
                                "Valid Name",
                                "Village",
                                null,
                                11.0,
                                185.0, // Invalid longitude
                                PreferredLanguage.ENGLISH);

                mockMvc.perform(post("/api/beekeepers/profile")
                                .header("Authorization", "Bearer " + testBeekeeperToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.success", is(false)));
        }

        @Test
        @DisplayName("8. Get seeded beekeeper profile returns 200 OK with details")
        void testGetSeededBeekeeperProfile() throws Exception {
                // Seeded beekeeper phone = 9876543213
                String seededToken = jwtService.generateToken("9876543213", "BEEKEEPER");

                mockMvc.perform(get("/api/beekeepers/profile")
                                .header("Authorization", "Bearer " + seededToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.data.kvicId", is("KVIC-TN-2024-001")))
                                .andExpect(jsonPath("$.data.name", is("Ramesh Kumar")))
                                .andExpect(jsonPath("$.data.village", is("Kotagiri, Nilgiris")))
                                .andExpect(jsonPath("$.data.verificationStatus", is("APPROVED")));
        }

        @Test
        @DisplayName("9. Update beekeeper profile returns 200 OK with modified fields")
        void testUpdateBeekeeperProfile() throws Exception {
                String phone = "9876543244";
                if (!userRepository.existsByPhoneNumber(phone)) {
                        userRepository.save(new User(phone, "pass", Role.BEEKEEPER, true));
                }
                String token = jwtService.generateToken(phone, "BEEKEEPER");

                // Create initial profile
                mockMvc.perform(post("/api/beekeepers/profile")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new BeekeeperProfileRequest(
                                                "KVIC-UPD-01", "Initial Name", "Initial Village", null, 11.0, 76.0,
                                                PreferredLanguage.ENGLISH))));

                // Update profile
                BeekeeperProfileUpdateRequest updateReq = new BeekeeperProfileUpdateRequest(
                                "Updated Name",
                                "Updated Village",
                                "https://example.com/new-photo.jpg",
                                12.5,
                                77.5,
                                PreferredLanguage.HINDI);

                mockMvc.perform(put("/api/beekeepers/profile")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateReq)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.data.name", is("Updated Name")))
                                .andExpect(jsonPath("$.data.village", is("Updated Village")))
                                .andExpect(jsonPath("$.data.preferredLanguage", is("HINDI")));
        }

        @Test
        @DisplayName("10. Customer token cannot access Beekeeper profile (403 Forbidden)")
        void testCustomerForbiddenFromBeekeeperProfile() throws Exception {
                String customerToken = jwtService.generateToken("9876543214", "CUSTOMER");

                mockMvc.perform(get("/api/beekeepers/profile")
                                .header("Authorization", "Bearer " + customerToken))
                                .andExpect(status().isForbidden())
                                .andExpect(jsonPath("$.success", is(false)));
        }

        @Test
        @DisplayName("11. Check onboarding status for completed vs uncompleted beekeeper")
        void testProfileStatusCheck() throws Exception {
                // Seeded beekeeper -> completed = true
                String seededToken = jwtService.generateToken("9876543213", "BEEKEEPER");
                mockMvc.perform(get("/api/beekeepers/profile/status")
                                .header("Authorization", "Bearer " + seededToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.completed", is(true)))
                                .andExpect(jsonPath("$.data.verificationStatus", is("APPROVED")));

                // New beekeeper without profile -> completed = false
                String newPhone = "9876543233";
                if (!userRepository.existsByPhoneNumber(newPhone)) {
                        userRepository.save(new User(newPhone, "pass", Role.BEEKEEPER, true));
                }
                String newToken = jwtService.generateToken(newPhone, "BEEKEEPER");
                mockMvc.perform(get("/api/beekeepers/profile/status")
                                .header("Authorization", "Bearer " + newToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.completed", is(false)));
        }
}
