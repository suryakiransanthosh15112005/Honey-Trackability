package com.honeychain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeychain.auth.dto.LoginRequest;
import com.honeychain.auth.dto.SendOtpRequest;
import com.honeychain.auth.dto.VerifyOtpRequest;
import com.honeychain.security.jwt.JwtService;
import com.honeychain.user.entity.Role;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    // =========================================================================
    // 1. Password Login Tests (Admin / Lab)
    // =========================================================================

    @Test
    @DisplayName("Admin login with valid credentials returns JWT and ADMIN role")
    void testAdminLoginSuccess() throws Exception {
        LoginRequest request = new LoginRequest("9876543210", "Admin@123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.role", is("ADMIN")));
    }

    @Test
    @DisplayName("Lab technician login with valid credentials returns JWT and LAB role")
    void testLabLoginSuccess() throws Exception {
        LoginRequest request = new LoginRequest("9876543212", "Lab@123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.role", is("LAB")));
    }

    @Test
    @DisplayName("Login with incorrect password returns error")
    void testLoginWithInvalidPassword() throws Exception {
        LoginRequest request = new LoginRequest("9876543210", "WrongPassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    @DisplayName("Login with non-existent user returns error")
    void testLoginWithUnknownUser() throws Exception {
        LoginRequest request = new LoginRequest("0000000000", "SomePassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)));
    }

    // =========================================================================
    // 2. OTP Authentication Tests (Beekeeper / Customer)
    // =========================================================================

    @Test
    @DisplayName("Send OTP successfully generates and dispatches OTP")
    void testSendOtpSuccess() throws Exception {
        SendOtpRequest request = new SendOtpRequest("9876543213", Role.BEEKEEPER);

        mockMvc.perform(post("/api/auth/send-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }

    @Test
    @DisplayName("Verify OTP with valid code returns JWT and BEEKEEPER role")
    void testVerifyOtpSuccess() throws Exception {
        mockMvc.perform(post("/api/auth/send-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new SendOtpRequest("9876543213", Role.BEEKEEPER))));

        VerifyOtpRequest verifyRequest = new VerifyOtpRequest("9876543213", "123456", Role.BEEKEEPER);

        mockMvc.perform(post("/api/auth/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.role", is("BEEKEEPER")));
    }

    @Test
    @DisplayName("Verify OTP with incorrect code returns error")
    void testVerifyOtpFailure() throws Exception {
        VerifyOtpRequest verifyRequest = new VerifyOtpRequest("9876543213", "000000", Role.BEEKEEPER);

        mockMvc.perform(post("/api/auth/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)));
    }

    // =========================================================================
    // 3. JWT Validation & Expiration Tests
    // =========================================================================

    @Test
    @DisplayName("JwtService generates and validates valid token correctly")
    void testJwtGenerationAndValidation() {
        String token = jwtService.generateToken("9876543210", "ADMIN");
        assertTrue(jwtService.isTokenValid(token));
        assertFalse(jwtService.isTokenExpired(token));
        org.junit.jupiter.api.Assertions.assertEquals("9876543210", jwtService.extractUsername(token));
        org.junit.jupiter.api.Assertions.assertEquals("ADMIN", jwtService.extractRole(token));
    }

    @Test
    @DisplayName("JwtService correctly identifies expired token")
    void testJwtExpiration() {
        String expiredToken = jwtService.generateTokenWithCustomExpiration("9876543210", "ADMIN", -1000);
        assertTrue(jwtService.isTokenExpired(expiredToken));
        assertFalse(jwtService.isTokenValid(expiredToken));
    }

    // =========================================================================
    // 4. Role-Based Access Control Tests
    // =========================================================================

    @Test
    @DisplayName("Protected endpoint without token returns 401 Unauthorized")
    void testProtectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/api/admin/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Protected endpoint with malformed token returns 401 Unauthorized")
    void testProtectedEndpointWithMalformedToken() throws Exception {
        mockMvc.perform(get("/api/admin/profile")
                        .header("Authorization", "Bearer this-is-not-a-valid-jwt-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Admin token can access /api/admin/profile (200 OK)")
    void testAdminAccessToAdminEndpoint() throws Exception {
        String adminToken = jwtService.generateToken("9876543210", "ADMIN");

        mockMvc.perform(get("/api/admin/profile")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }

    @Test
    @DisplayName("KVIC Officer token can access /api/admin/profile (200 OK)")
    void testKvicAccessToAdminEndpoint() throws Exception {
        String kvicToken = jwtService.generateToken("9876543211", "KVIC_OFFICER");

        mockMvc.perform(get("/api/admin/profile")
                        .header("Authorization", "Bearer " + kvicToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }

    @Test
    @DisplayName("Beekeeper token trying to access /api/admin/profile returns 403 Forbidden")
    void testBeekeeperForbiddenFromAdminEndpoint() throws Exception {
        String beekeeperToken = jwtService.generateToken("9876543213", "BEEKEEPER");

        mockMvc.perform(get("/api/admin/profile")
                        .header("Authorization", "Bearer " + beekeeperToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    @DisplayName("Customer token trying to access /api/beekeepers/profile returns 403 Forbidden")
    void testCustomerForbiddenFromBeekeeperEndpoint() throws Exception {
        String customerToken = jwtService.generateToken("9876543214", "CUSTOMER");

        mockMvc.perform(get("/api/beekeepers/profile")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    @DisplayName("Beekeeper token can access /api/beekeepers/profile (200 OK)")
    void testBeekeeperAccessToOwnEndpoint() throws Exception {
        String beekeeperToken = jwtService.generateToken("9876543213", "BEEKEEPER");

        mockMvc.perform(get("/api/beekeepers/profile")
                        .header("Authorization", "Bearer " + beekeeperToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }

    @Test
    @DisplayName("Lab token can access /api/lab/profile (200 OK)")
    void testLabAccessToLabEndpoint() throws Exception {
        String labToken = jwtService.generateToken("9876543212", "LAB");

        mockMvc.perform(get("/api/lab/profile")
                        .header("Authorization", "Bearer " + labToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }

    @Test
    @DisplayName("Public health endpoint is accessible without token (200 OK)")
    void testHealthEndpointPublic() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("HoneyChain backend is running")));
    }
}
