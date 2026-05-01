package com.timetracker.task_time_tracker.dto;

import com.timetracker.task_time_tracker.model.Task;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ с данными задачи")
public record TaskResponse(

        @Schema(description = "Уникальный идентификатор задачи", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        @Schema(description = "Название задачи", example = "Разработка API")
        String name,

        @Schema(description = "Описание задачи", example = "Создать REST-сервис для учёта времени")
        String description,

        @Schema(description = "Текущий статус задачи", example = "IN_PROGRESS")
        TaskStatus status
) {
    public static TaskResponse fromEntity(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getName(),
                task.getDescription(),
                TaskStatus.fromValue(task.getStatus())
        );
    }
}