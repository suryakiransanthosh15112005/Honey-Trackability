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
class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Public user can browse marketplace without authentication (200 OK)")
    void testPublicMarketplaceAccessPermitted() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Unauthenticated user cannot create product listing (401 Unauthorized)")
    void testUnauthenticatedCreateProductDenied() throws Exception {
        mockMvc.perform(post("/api/beekeepers/products"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "9876543214", roles = {"CUSTOMER"})
    @DisplayName("CUSTOMER role cannot create beekeeper product listing (403 Forbidden)")
    void testCustomerCreateProductForbidden() throws Exception {
        mockMvc.perform(post("/api/beekeepers/products"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "9876543213", roles = {"BEEKEEPER"})
    @DisplayName("BEEKEEPER role can view own product listings")
    void testBeekeeperViewOwnProducts() throws Exception {
        mockMvc.perform(get("/api/beekeepers/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
