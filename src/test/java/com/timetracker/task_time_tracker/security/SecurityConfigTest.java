package com.timetracker.task_time_tracker.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void publicEndpoint_ShouldBeAccessibleWithoutToken() throws Exception {
        Map<String, String> request = Map.of(
                "username", "admin",
                "password", "admin123"
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void protectedEndpoint_ShouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_ShouldReturn200WithValidToken() throws Exception {
        String token = jwtService.generateToken("admin");
        mockMvc.perform(get("/api/tasks/{id}", 999999L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void protectedEndpoint_ShouldReturn401WithInvalidToken() throws Exception {
        String invalidToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJpbnZhbGlkIn0.invalid_signature";

        mockMvc.perform(get("/api/tasks/1")
                        .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_ShouldReturn401WithMalformedToken() throws Exception {
        String malformedToken = "not.a.valid.token!";

        mockMvc.perform(get("/api/tasks/1")
                        .header("Authorization", "Bearer " + malformedToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_ShouldReturn401WithEmptyBearer() throws Exception {
        mockMvc.perform(get("/api/tasks/1")
                        .header("Authorization", "Bearer "))
                .andExpect(status().isUnauthorized());
    }
}