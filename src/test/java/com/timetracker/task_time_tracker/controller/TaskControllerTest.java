package com.timetracker.task_time_tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timetracker.task_time_tracker.dto.TaskCreateDTO;
import com.timetracker.task_time_tracker.dto.TaskStatus;
import com.timetracker.task_time_tracker.dto.TaskUpdateStatusDTO;
import com.timetracker.task_time_tracker.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    private String adminToken;

    @BeforeEach
    void setUp() {
        adminToken = jwtService.generateToken("admin");
    }

    @Test
    void createTask_ShouldReturn201() throws Exception {
        TaskCreateDTO dto = new TaskCreateDTO(
                "Тестовая задача",
                "Описание тестовой задачи",
                TaskStatus.NEW
        );

        mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Тестовая задача"))
                .andExpect(jsonPath("$.status").value("NEW"));
    }

    @Test
    void createTask_WithoutToken_ShouldReturn401() throws Exception {
        TaskCreateDTO dto = new TaskCreateDTO(
                "Тестовая задача",
                "Описание",
                TaskStatus.NEW
        );

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createTask_WithInvalidName_ShouldReturn400() throws Exception {
        TaskCreateDTO dto = new TaskCreateDTO(
                "AB",
                "Описание",
                TaskStatus.NEW
        );

        mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTaskById_ShouldReturn200() throws Exception {
        TaskCreateDTO createDto = new TaskCreateDTO(
                "Задача для поиска",
                "Описание",
                TaskStatus.NEW
        );

        String response = mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/api/tasks/{id}", id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Задача для поиска"));
    }

    @Test
    void getTaskById_NotFound_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/tasks/{id}", 99999)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTaskStatus_ShouldReturn204() throws Exception {
        TaskCreateDTO createDto = new TaskCreateDTO(
                "Задача для смены статуса",
                "Описание",
                TaskStatus.NEW
        );

        String response = mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        TaskUpdateStatusDTO updateDto = new TaskUpdateStatusDTO(TaskStatus.IN_PROGRESS);

        mockMvc.perform(patch("/api/tasks/{id}/status", id)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNoContent());
    }
}