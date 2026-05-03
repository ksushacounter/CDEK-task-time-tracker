package com.timetracker.task_time_tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timetracker.task_time_tracker.dto.TaskCreateDTO;
import com.timetracker.task_time_tracker.dto.TaskStatus;
import com.timetracker.task_time_tracker.dto.TimeRecordCreateDTO;
import com.timetracker.task_time_tracker.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TimeRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    private String adminToken;
    private Long taskId;

    @BeforeEach
    void setUp() throws Exception {
        adminToken = jwtService.generateToken("admin");

        TaskCreateDTO taskDto = new TaskCreateDTO(
                "Тестовая задача для TimeRecord",
                "Описание",
                TaskStatus.NEW
        );

        String response = mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        taskId = objectMapper.readTree(response).get("id").asLong();
    }

    @Test
    void createTimeRecord_ShouldReturn201() throws Exception {
        TimeRecordCreateDTO dto = new TimeRecordCreateDTO(
                1L,
                taskId,
                LocalDateTime.of(2024, 6, 1, 9, 0, 0),
                LocalDateTime.of(2024, 6, 1, 12, 30, 0),
                "Разработка функционала"
        );

        mockMvc.perform(post("/api/time-records")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.employeeId").value(1L))
                .andExpect(jsonPath("$.taskId").value(taskId))
                .andExpect(jsonPath("$.durationMinutes").value(210))
                .andExpect(jsonPath("$.description").value("Разработка функционала"));
    }

    @Test
    void createTimeRecord_WithoutToken_ShouldReturn401() throws Exception {
        TimeRecordCreateDTO dto = new TimeRecordCreateDTO(
                1L,
                taskId,
                LocalDateTime.of(2024, 6, 1, 9, 0, 0),
                LocalDateTime.of(2024, 6, 1, 12, 30, 0),
                "Описание"
        );

        mockMvc.perform(post("/api/time-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createTimeRecord_WithInvalidTimeRange_ShouldReturn400() throws Exception {
        TimeRecordCreateDTO dto = new TimeRecordCreateDTO(
                1L,
                taskId,
                LocalDateTime.of(2024, 6, 1, 17, 0, 0),
                LocalDateTime.of(2024, 6, 1, 12, 0, 0),
                "Неверный интервал"
        );

        mockMvc.perform(post("/api/time-records")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTimeRecord_WithNullEmployeeId_ShouldReturn400() throws Exception {
        String invalidJson = """
                {
                    "taskId": %d,
                    "startTime": "2024-06-01T09:00:00",
                    "endTime": "2024-06-01T12:30:00"
                }
                """.formatted(taskId);

        mockMvc.perform(post("/api/time-records")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getByEmployeeAndPeriod_ShouldReturn200() throws Exception {
        TimeRecordCreateDTO createDto = new TimeRecordCreateDTO(
                1L,
                taskId,
                LocalDateTime.of(2024, 6, 1, 9, 0, 0),
                LocalDateTime.of(2024, 6, 1, 12, 0, 0),
                "Утренняя работа"
        );

        mockMvc.perform(post("/api/time-records")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/time-records/employee/{employeeId}", 1L)
                        .header("Authorization", "Bearer " + adminToken)
                        .param("startDate", "2024-06-01T00:00:00")
                        .param("endDate", "2024-06-30T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].employeeId").value(1L))
                .andExpect(jsonPath("$[0].taskId").value(taskId));
    }

    @Test
    void getByEmployeeAndPeriod_WithoutToken_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/time-records/employee/{employeeId}", 1L)
                        .param("startDate", "2024-06-01T00:00:00")
                        .param("endDate", "2024-06-30T23:59:59"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createTimeRecord_WithFutureStartTime_ShouldReturn400() throws Exception {
        TimeRecordCreateDTO dto = new TimeRecordCreateDTO(
                1L,
                taskId,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(3),
                "Запланированная работа"
        );

        mockMvc.perform(post("/api/time-records")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTimeRecord_WithNonExistentTask_ShouldReturn500() throws Exception {
        TimeRecordCreateDTO dto = new TimeRecordCreateDTO(
                1L,
                99999L,
                LocalDateTime.of(2024, 6, 1, 9, 0, 0),
                LocalDateTime.of(2024, 6, 1, 12, 0, 0),
                "Несуществующая задача"
        );

        mockMvc.perform(post("/api/time-records")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }
}