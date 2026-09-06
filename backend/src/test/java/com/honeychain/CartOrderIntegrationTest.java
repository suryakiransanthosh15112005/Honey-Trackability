package com.honeychain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CartOrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Unauthenticated user cannot access cart (401 Unauthorized)")
    void testUnauthenticatedCartAccessDenied() throws Exception {
        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "9876543214", roles = { "CUSTOMER" })
    @DisplayName("CUSTOMER role can access cart (200 OK)")
    void testCustomerCartAccessAllowed() throws Exception {
        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "9876543213", roles = { "BEEKEEPER" })
    @DisplayName("BEEKEEPER role cannot access customer cart (403 Forbidden)")
    void testBeekeeperCartAccessForbidden() throws Exception {
        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "9876543214", roles = { "CUSTOMER" })
    @DisplayName("CUSTOMER role can access order history (200 OK)")
    void testCustomerOrdersAccessAllowed() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "9876543213", roles = { "BEEKEEPER" })
    @DisplayName("BEEKEEPER role can access beekeeper order management (200 OK)")
    void testBeekeeperOrdersAccessAllowed() throws Exception {
        mockMvc.perform(get("/api/beekeepers/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
