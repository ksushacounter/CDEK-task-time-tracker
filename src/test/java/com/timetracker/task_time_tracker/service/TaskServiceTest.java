package com.timetracker.task_time_tracker.service;

import com.timetracker.task_time_tracker.dto.TaskCreateDTO;
import com.timetracker.task_time_tracker.dto.TaskResponse;
import com.timetracker.task_time_tracker.dto.TaskStatus;
import com.timetracker.task_time_tracker.dto.TaskUpdateStatusDTO;
import com.timetracker.task_time_tracker.exсeption.EntityNotFoundException;
import com.timetracker.task_time_tracker.exсeption.InvalidStatusTransitionException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class TaskServiceTest {

    @Autowired
    private TaskService taskService;

    @Test
    void createTask_ShouldSaveAndReturnTask() {
        TaskCreateDTO dto = new TaskCreateDTO(
                "Сервисный тест",
                "Тестирование сервисного слоя",
                TaskStatus.NEW
        );

        TaskResponse response = taskService.create(dto);

        assertThat(response.id()).isNotNull();
        assertThat(response.name()).isEqualTo("Сервисный тест");
        assertThat(response.status()).isEqualTo(TaskStatus.NEW);
    }

    @Test
    void getTaskById_ShouldReturnTask() {
        TaskCreateDTO dto = new TaskCreateDTO(
                "Поиск по ID",
                "Описание",
                TaskStatus.NEW
        );

        TaskResponse created = taskService.create(dto);
        TaskResponse found = taskService.getById(created.id());

        assertThat(found.id()).isEqualTo(created.id());
        assertThat(found.name()).isEqualTo("Поиск по ID");
    }

    @Test
    void getTaskById_NotFound_ShouldThrowException() {
        assertThatThrownBy(() -> taskService.getById(99999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Task with id 99999 not found");
    }

    @Test
    void updateStatus_ShouldChangeStatus() {
        TaskCreateDTO dto = new TaskCreateDTO(
                "Смена статуса",
                "Описание",
                TaskStatus.NEW
        );

        TaskResponse created = taskService.create(dto);
        TaskUpdateStatusDTO updateDto = new TaskUpdateStatusDTO(TaskStatus.IN_PROGRESS);
        TaskResponse updated = taskService.updateStatus(created.id(), updateDto);

        assertThat(updated.status()).isEqualTo(TaskStatus.IN_PROGRESS);
    }

    @Test
    void updateStatus_FromDoneToOther_ShouldThrowException() {
        TaskCreateDTO dto = new TaskCreateDTO(
                "Done задача",
                "Описание",
                TaskStatus.NEW
        );

        TaskResponse created = taskService.create(dto);
        taskService.updateStatus(created.id(), new TaskUpdateStatusDTO(TaskStatus.DONE));

        assertThatThrownBy(() -> taskService.updateStatus(created.id(), new TaskUpdateStatusDTO(TaskStatus.IN_PROGRESS)))
                .isInstanceOf(InvalidStatusTransitionException.class);
    }
}