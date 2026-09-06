package com.honeychain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OpenApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Verify /v3/api-docs generates valid OpenAPI 3.0 documentation with all required endpoints and multipart schemas")
    void testOpenApiDocsGenerated() throws Exception {
        MvcResult result = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        
        // Assert exact customer profile endpoints
        org.junit.jupiter.api.Assertions.assertTrue(json.contains("\"/api/customers/profile\""), "Missing /api/customers/profile path");
        org.junit.jupiter.api.Assertions.assertTrue(json.contains("\"/api/customers/profile/status\""), "Missing /api/customers/profile/status path");
        
        // Assert exact customer dispute endpoints
        org.junit.jupiter.api.Assertions.assertTrue(json.contains("\"/api/disputes\""), "Missing /api/disputes path");
        org.junit.jupiter.api.Assertions.assertTrue(json.contains("\"/api/disputes/my\""), "Missing /api/disputes/my path");
        org.junit.jupiter.api.Assertions.assertTrue(json.contains("\"/api/disputes/{id}\""), "Missing /api/disputes/{id} path");

        // Assert multipart content types in API docs
        org.junit.jupiter.api.Assertions.assertTrue(json.contains("multipart/form-data"), "Missing multipart/form-data content type documentation");
    }
}
