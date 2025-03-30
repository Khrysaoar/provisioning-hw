package com.voxloud.provisioning.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class ProvisioningIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should return configuration file when device exists in inventory")
    void getProvisioning_shouldReturnConfig_whenMacExists() throws Exception {
        mockMvc.perform(get("/api/v1/provisioning/a1-b2-c3-d4-e5-f6"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("username")))
                .andExpect(content().string(containsString("password")));
    }

    @Test
    @DisplayName("Should return 404 when device is not found in inventory")
    void getProvisioning_shouldReturn404_whenMacNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/provisioning/00-00-00-00-00-00"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Device not found")));
    }
}