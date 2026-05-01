package com.timetracker.task_time_tracker.controller;

import com.timetracker.task_time_tracker.dto.TaskCreateDTO;
import com.timetracker.task_time_tracker.dto.TaskResponse;
import com.timetracker.task_time_tracker.dto.TaskUpdateStatusDTO;
import com.timetracker.task_time_tracker.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Управление задачами (CRUD операции)")
@SecurityRequirement(name = "Bearer Authentication")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать новую задачу", description = "Создаёт задачу с начальным статусом NEW")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Задача успешно создана"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации")
    })
    public TaskResponse create(@Valid @RequestBody TaskCreateDTO dto) {
        return taskService.create(dto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить задачу по ID", description = "Возвращает полную информацию о задаче")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача найдена"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    public TaskResponse getById(@Parameter(description = "ID задачи", example = "1") @PathVariable Long id) {
        return taskService.getById(id);
    }

    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Изменить статус задачи", description = "Обновляет статус задачи с проверкой допустимых переходов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Статус успешно изменён"),
            @ApiResponse(responseCode = "400", description = "Недопустимый переход статуса"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    public void updateStatus(@PathVariable Long id, @Valid @RequestBody TaskUpdateStatusDTO dto) {
        taskService.updateStatus(id, dto);
    }
}