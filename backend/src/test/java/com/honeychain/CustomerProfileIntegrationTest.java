package com.honeychain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeychain.customer.dto.CustomerProfileRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CustomerProfileIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String CUSTOMER_PHONE = "9876543214";

    @Test
    @Order(1)
    @DisplayName("1. Unauthenticated request to /api/customers/profile fails (401 Unauthorized)")
    void testUnauthenticatedProfileAccess() throws Exception {
        mockMvc.perform(get("/api/customers/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(2)
    @WithMockUser(username = "9876543213", roles = { "BEEKEEPER" })
    @DisplayName("2. Non-CUSTOMER role cannot access /api/customers/profile (403 Forbidden)")
    void testNonCustomerProfileAccessForbidden() throws Exception {
        mockMvc.perform(get("/api/customers/profile"))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(3)
    @WithMockUser(username = CUSTOMER_PHONE, roles = { "CUSTOMER" })
    @DisplayName("3. GET /api/customers/profile/status before profile creation returns setup incomplete")
    void testProfileStatusIncomplete() throws Exception {
        mockMvc.perform(get("/api/customers/profile/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.profileExists", is(false)))
                .andExpect(jsonPath("$.data.complete", is(false)));
    }

    @Test
    @Order(4)
    @WithMockUser(username = CUSTOMER_PHONE, roles = { "CUSTOMER" })
    @DisplayName("4. POST /api/customers/profile creates profile successfully (201 Created)")
    void testCreateProfile() throws Exception {
        CustomerProfileRequest request = new CustomerProfileRequest(
                "Ananya Sharma",
                "ananya@example.com",
                "123 MG Road",
                "Bengaluru",
                "Karnataka",
                "560001",
                "http://example.com/avatar.jpg"
        );

        mockMvc.perform(post("/api/customers/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.fullName", is("Ananya Sharma")))
                .andExpect(jsonPath("$.data.city", is("Bengaluru")));
    }

    @Test
    @Order(5)
    @WithMockUser(username = CUSTOMER_PHONE, roles = { "CUSTOMER" })
    @DisplayName("5. Duplicate POST /api/customers/profile fails (400 Bad Request)")
    void testDuplicateCreateProfileFails() throws Exception {
        CustomerProfileRequest request = new CustomerProfileRequest(
                "Ananya Duplicate",
                "ananya2@example.com",
                "123 MG Road",
                "Bengaluru",
                "Karnataka",
                "560001",
                null
        );

        mockMvc.perform(post("/api/customers/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(6)
    @WithMockUser(username = CUSTOMER_PHONE, roles = { "CUSTOMER" })
    @DisplayName("6. GET /api/customers/profile returns persisted profile data")
    void testGetProfile() throws Exception {
        mockMvc.perform(get("/api/customers/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.fullName", is("Ananya Sharma")))
                .andExpect(jsonPath("$.data.phoneNumber", is(CUSTOMER_PHONE)));
    }

    @Test
    @Order(7)
    @WithMockUser(username = CUSTOMER_PHONE, roles = { "CUSTOMER" })
    @DisplayName("7. PUT /api/customers/profile updates profile data (200 OK)")
    void testUpdateProfile() throws Exception {
        CustomerProfileRequest updateRequest = new CustomerProfileRequest(
                "Ananya S. Kumar",
                "ananya.updated@example.com",
                "456 Indiranagar",
                "Bengaluru",
                "Karnataka",
                "560038",
                "http://example.com/new-avatar.jpg"
        );

        mockMvc.perform(put("/api/customers/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.fullName", is("Ananya S. Kumar")))
                .andExpect(jsonPath("$.data.address", is("456 Indiranagar")));
    }

    @Test
    @Order(8)
    @WithMockUser(username = CUSTOMER_PHONE, roles = { "CUSTOMER" })
    @DisplayName("8. GET /api/customers/profile/status after profile creation returns setup complete")
    void testProfileStatusComplete() throws Exception {
        mockMvc.perform(get("/api/customers/profile/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.profileExists", is(true)))
                .andExpect(jsonPath("$.data.complete", is(true)));
    }
}
