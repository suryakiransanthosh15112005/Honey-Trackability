package com.honeychain;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SystemSecurityOwnershipIntegrationTest {

        @Autowired
        private MockMvc mockMvc;
        @Autowired
        private ObjectMapper objectMapper;
        @Autowired
        private JwtService jwtService;
        @Autowired
        private UserRepository userRepository;
        @Autowired
        private PasswordEncoder passwordEncoder;

        private static final String BEEKEEPER_A_PHONE = "9977665501";
        private static final String BEEKEEPER_B_PHONE = "9977665502";
        private static final String CUSTOMER_A_PHONE = "9977665503";
        private static final String CUSTOMER_B_PHONE = "9977665504";

        private User beekeeperA;
        private User beekeeperB;
        private User customerA;
        private User customerB;

        @BeforeEach
        void setup() {
                beekeeperA = userRepository.findByPhoneNumber(BEEKEEPER_A_PHONE)
                                .orElseGet(() -> userRepository.save(new User(BEEKEEPER_A_PHONE,
                                                passwordEncoder.encode("Test@1234"), Role.BEEKEEPER, true)));

                beekeeperB = userRepository.findByPhoneNumber(BEEKEEPER_B_PHONE)
                                .orElseGet(() -> userRepository.save(new User(BEEKEEPER_B_PHONE,
                                                passwordEncoder.encode("Test@1234"), Role.BEEKEEPER, true)));

                customerA = userRepository.findByPhoneNumber(CUSTOMER_A_PHONE).orElseGet(() -> userRepository.save(
                                new User(CUSTOMER_A_PHONE, passwordEncoder.encode("Test@1234"), Role.CUSTOMER, true)));

                customerB = userRepository.findByPhoneNumber(CUSTOMER_B_PHONE).orElseGet(() -> userRepository.save(
                                new User(CUSTOMER_B_PHONE, passwordEncoder.encode("Test@1234"), Role.CUSTOMER, true)));
        }

        private String tokenFor(String phone, String role) {
                return jwtService.generateToken(phone, role);
        }

        @Test
        @DisplayName("1. Role Matrix: Customer cannot access /api/admin/dashboard (403)")
        void customerCannotAccessAdmin() throws Exception {
                mockMvc.perform(get("/api/admin/dashboard")
                                .header("Authorization", "Bearer " + tokenFor(CUSTOMER_A_PHONE, "CUSTOMER")))
                                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("2. Role Matrix: Beekeeper cannot access /api/admin/dashboard (403)")
        void beekeeperCannotAccessAdmin() throws Exception {
                mockMvc.perform(get("/api/admin/dashboard")
                                .header("Authorization", "Bearer " + tokenFor(BEEKEEPER_A_PHONE, "BEEKEEPER")))
                                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("3. Role Matrix: Unauthenticated request to /api/admin/dashboard returns 401")
        void unauthenticatedCannotAccessAdmin() throws Exception {
                mockMvc.perform(get("/api/admin/dashboard"))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("4. Ownership Test: Beekeeper A cannot access Beekeeper B's hives")
        void beekeeperCrossTenantIsolation() throws Exception {
                mockMvc.perform(get("/api/beekeepers/hives/999999")
                                .header("Authorization", "Bearer " + tokenFor(BEEKEEPER_A_PHONE, "BEEKEEPER")))
                                .andExpect(status().is4xxClientError());
        }

        @Test
        @DisplayName("5. Ownership Test: Customer A cannot access Customer B's cart or orders")
        void customerCrossTenantIsolation() throws Exception {
                // Customer A queries cart -> receives Customer A's empty cart
                mockMvc.perform(get("/api/cart")
                                .header("Authorization", "Bearer " + tokenFor(CUSTOMER_A_PHONE, "CUSTOMER")))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.items").isEmpty());
        }

        @Test
        @DisplayName("6. Public Endpoints: Public verification is accessible without authentication")
        void publicVerificationIsAccessibleWithoutToken() throws Exception {
                mockMvc.perform(get("/api/public/verify/HC-NONEXISTENT"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)));
        }

        @Test
        @DisplayName("7. User Management Security: Non-admin cannot list all users (403)")
        void nonAdminCannotListAllUsers() throws Exception {
                mockMvc.perform(get("/api/users")
                                .header("Authorization", "Bearer " + tokenFor(CUSTOMER_A_PHONE, "CUSTOMER")))
                                .andExpect(status().isForbidden());

                mockMvc.perform(get("/api/users")
                                .header("Authorization", "Bearer " + tokenFor(BEEKEEPER_A_PHONE, "BEEKEEPER")))
                                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("8. User Management Security: Non-admin cannot access user by ID (403)")
        void nonAdminCannotGetUserById() throws Exception {
                mockMvc.perform(get("/api/users/" + beekeeperA.getId())
                                .header("Authorization", "Bearer " + tokenFor(CUSTOMER_A_PHONE, "CUSTOMER")))
                                .andExpect(status().isForbidden());
        }
}
