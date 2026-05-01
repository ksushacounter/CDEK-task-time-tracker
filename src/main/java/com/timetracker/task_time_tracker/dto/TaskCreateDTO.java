package com.timetracker.task_time_tracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO для создания новой задачи")
public record TaskCreateDTO(

        @Schema(description = "Название задачи", example = "Разработка API", minLength = 3, maxLength = 100)
        @NotBlank(message = "Название задачи обязательно")
        @Size(min = 3, max = 100, message = "Название должно быть от 3 до 100 символов")
        String name,

        @Schema(description = "Подробное описание задачи", example = "Создать REST-сервис для учёта времени", maxLength = 500)
        @Size(max = 500, message = "Описание не может превышать 500 символов")
        String description,

        @Schema(description = "Текущий статус задачи", example = "NEW", allowableValues = {"NEW", "IN_PROGRESS", "DONE"})
        @NotNull(message = "Статус задачи обязателен")
        TaskStatus status
) {
}