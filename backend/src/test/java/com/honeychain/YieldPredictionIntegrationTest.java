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
class YieldPredictionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Unauthenticated user cannot access yield prediction endpoints (401)")
    void testUnauthenticatedAccessDenied() throws Exception {
        mockMvc.perform(get("/api/beekeepers/hives/1/yield-prediction"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "9876543214", roles = {"CUSTOMER"})
    @DisplayName("CUSTOMER role cannot access beekeeper yield prediction endpoints (403)")
    void testCustomerAccessForbidden() throws Exception {
        mockMvc.perform(get("/api/beekeepers/hives/1/yield-prediction"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "9876543213", roles = {"BEEKEEPER"})
    @DisplayName("BEEKEEPER can fetch all owned hive predictions")
    void testBeekeeperFetchAllPredictions() throws Exception {
        mockMvc.perform(get("/api/beekeepers/hives/yield-predictions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
