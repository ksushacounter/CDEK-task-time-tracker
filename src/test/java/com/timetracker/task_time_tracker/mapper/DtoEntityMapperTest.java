package com.timetracker.task_time_tracker.mapper;

import com.timetracker.task_time_tracker.dto.TaskCreateDTO;
import com.timetracker.task_time_tracker.dto.TaskResponse;
import com.timetracker.task_time_tracker.dto.TaskStatus;
import com.timetracker.task_time_tracker.dto.TimeRecordCreateDTO;
import com.timetracker.task_time_tracker.dto.TimeRecordResponse;
import com.timetracker.task_time_tracker.model.Task;
import com.timetracker.task_time_tracker.model.TimeRecord;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DtoEntityMapperTest {

    private final DtoEntityMapper mapper = new DtoEntityMapper();

    @Test
    void toEntity_ShouldMapTaskCreateDTO() {
        TaskCreateDTO dto = new TaskCreateDTO(
                "Тестовая задача",
                "Подробное описание",
                TaskStatus.IN_PROGRESS
        );

        Task task = mapper.toEntity(dto);

        assertThat(task.getId()).isNull();
        assertThat(task.getName()).isEqualTo("Тестовая задача");
        assertThat(task.getDescription()).isEqualTo("Подробное описание");
        assertThat(task.getStatus()).isEqualTo("IN_PROGRESS");
    }

    @Test
    void toEntity_ShouldHandleNullDescription() {
        TaskCreateDTO dto = new TaskCreateDTO(
                "Задача без описания",
                null,
                TaskStatus.NEW
        );

        Task task = mapper.toEntity(dto);

        assertThat(task.getName()).isEqualTo("Задача без описания");
        assertThat(task.getDescription()).isNull();
        assertThat(task.getStatus()).isEqualTo("NEW");
    }

    @Test
    void toEntity_ShouldMapTimeRecordCreateDTO() {
        TimeRecordCreateDTO dto = new TimeRecordCreateDTO(
                10L, 20L,
                LocalDateTime.of(2024, 6, 1, 9, 0),
                LocalDateTime.of(2024, 6, 1, 17, 0),
                "Полный рабочий день"
        );

        TimeRecord record = mapper.toEntity(dto);

        assertThat(record.getId()).isNull();
        assertThat(record.getEmployeeId()).isEqualTo(10L);
        assertThat(record.getTaskId()).isEqualTo(20L);
        assertThat(record.getStartTime()).isEqualTo(LocalDateTime.of(2024, 6, 1, 9, 0));
        assertThat(record.getEndTime()).isEqualTo(LocalDateTime.of(2024, 6, 1, 17, 0));
        assertThat(record.getDescription()).isEqualTo("Полный рабочий день");
    }

    @Test
    void toResponse_ShouldMapTaskToTaskResponse() {
        Task task = new Task(100L, "Готовая задача", "Описание", "DONE");

        TaskResponse response = mapper.toResponse(task);

        assertThat(response.id()).isEqualTo(100L);
        assertThat(response.name()).isEqualTo("Готовая задача");
        assertThat(response.description()).isEqualTo("Описание");
        assertThat(response.status()).isEqualTo(TaskStatus.DONE);
    }

    @Test
    void toResponse_ShouldMapTimeRecordToTimeRecordResponse() {
        TimeRecord record = new TimeRecord(
                200L, 1L, 2L,
                LocalDateTime.of(2024, 6, 1, 9, 0),
                LocalDateTime.of(2024, 6, 1, 12, 30),
                "Работа над задачей"
        );

        TimeRecordResponse response = mapper.toResponse(record);

        assertThat(response.id()).isEqualTo(200L);
        assertThat(response.employeeId()).isEqualTo(1L);
        assertThat(response.taskId()).isEqualTo(2L);
        assertThat(response.startTime()).isEqualTo(LocalDateTime.of(2024, 6, 1, 9, 0));
        assertThat(response.endTime()).isEqualTo(LocalDateTime.of(2024, 6, 1, 12, 30));
        assertThat(response.durationMinutes()).isEqualTo(210);
        assertThat(response.description()).isEqualTo("Работа над задачей");
    }
}