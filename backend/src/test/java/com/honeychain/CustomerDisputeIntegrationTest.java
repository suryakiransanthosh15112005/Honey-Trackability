package com.honeychain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeychain.admin.dto.CreateDisputeRequest;
import com.honeychain.admin.dto.DisputeStatusUpdateRequest;
import com.honeychain.admin.entity.DisputeStatus;
import com.honeychain.user.entity.Role;
import com.honeychain.user.entity.User;
import com.honeychain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CustomerDisputeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String CUSTOMER_A_PHONE = "9876543214";
    private static final String CUSTOMER_B_PHONE = "9876543299";
    private static final String ADMIN_PHONE = "9876543210";

    private static Long createdDisputeId;

    @BeforeEach
    void setUp() {
        if (userRepository.findByPhoneNumber(CUSTOMER_B_PHONE).isEmpty()) {
            userRepository.save(new User(CUSTOMER_B_PHONE, passwordEncoder.encode("Password@123"), Role.CUSTOMER));
        }
    }

    @Test
    @Order(1)
    @WithMockUser(username = CUSTOMER_A_PHONE, roles = { "CUSTOMER" })
    @DisplayName("1. CUSTOMER A creates a new dispute (201 Created)")
    void testCreateDisputeCustomerA() throws Exception {
        CreateDisputeRequest request = new CreateDisputeRequest(
                "HC-2026-DISPUTE01",
                "ORD-998811",
                "Honey seal broken and suspect adulteration",
                "Packaging arrived damaged and color seems off."
        );

        MvcResult result = mockMvc.perform(post("/api/disputes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.batchId", is("HC-2026-DISPUTE01")))
                .andExpect(jsonPath("$.data.status", is("OPEN")))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        createdDisputeId = objectMapper.readTree(content).at("/data/id").asLong();
    }

    @Test
    @Order(2)
    @WithMockUser(username = CUSTOMER_A_PHONE, roles = { "CUSTOMER" })
    @DisplayName("2. CUSTOMER A gets own disputes list (GET /api/disputes/my)")
    void testGetMyDisputesCustomerA() throws Exception {
        mockMvc.perform(get("/api/disputes/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].id", is(createdDisputeId.intValue())));
    }

    @Test
    @Order(3)
    @WithMockUser(username = CUSTOMER_A_PHONE, roles = { "CUSTOMER" })
    @DisplayName("3. CUSTOMER A gets details of own dispute (GET /api/disputes/{id})")
    void testGetDisputeByIdCustomerA() throws Exception {
        mockMvc.perform(get("/api/disputes/" + createdDisputeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.id", is(createdDisputeId.intValue())))
                .andExpect(jsonPath("$.data.batchId", is("HC-2026-DISPUTE01")));
    }

    @Test
    @Order(4)
    @WithMockUser(username = CUSTOMER_B_PHONE, roles = { "CUSTOMER" })
    @DisplayName("4. SECURITY ISOLATION: CUSTOMER B cannot access CUSTOMER A dispute (404 Not Found)")
    void testSecurityIsolationDisputeById() throws Exception {
        mockMvc.perform(get("/api/disputes/" + createdDisputeId))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(5)
    @WithMockUser(username = CUSTOMER_B_PHONE, roles = { "CUSTOMER" })
    @DisplayName("5. SECURITY ISOLATION: CUSTOMER B /api/disputes/my does not contain CUSTOMER A dispute")
    void testSecurityIsolationMyDisputes() throws Exception {
        mockMvc.perform(get("/api/disputes/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content", hasSize(0)));
    }

    @Test
    @Order(6)
    @WithMockUser(username = ADMIN_PHONE, roles = { "ADMIN" })
    @DisplayName("6. ADMIN retrieves all disputes (GET /api/admin/disputes)")
    void testAdminGetDisputes() throws Exception {
        mockMvc.perform(get("/api/admin/disputes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content", hasSize(1)));
    }

    @Test
    @Order(7)
    @WithMockUser(username = ADMIN_PHONE, roles = { "ADMIN" })
    @DisplayName("7. ADMIN updates dispute status (PATCH /api/admin/disputes/{id}/status)")
    void testAdminUpdateDisputeStatus() throws Exception {
        DisputeStatusUpdateRequest request = new DisputeStatusUpdateRequest(
                DisputeStatus.INVESTIGATING,
                "Assigned lab officer to review batch sample."
        );

        mockMvc.perform(patch("/api/admin/disputes/" + createdDisputeId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("INVESTIGATING")))
                .andExpect(jsonPath("$.data.resolutionNotes", is("Assigned lab officer to review batch sample.")));
    }

    @Test
    @Order(8)
    @WithMockUser(username = CUSTOMER_A_PHONE, roles = { "CUSTOMER" })
    @DisplayName("8. CUSTOMER cannot access admin dispute endpoints (403 Forbidden)")
    void testCustomerAdminDisputeForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/disputes"))
                .andExpect(status().isForbidden());
    }
}
