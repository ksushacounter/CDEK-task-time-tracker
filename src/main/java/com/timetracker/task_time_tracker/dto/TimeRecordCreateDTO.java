package com.timetracker.task_time_tracker.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.timetracker.task_time_tracker.validation.ValidTimeRange;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@ValidTimeRange
@Schema(description = "DTO для создания записи о затраченном времени")
public record TimeRecordCreateDTO(

        @Schema(description = "ID сотрудника", example = "1", required = true)
        @NotNull(message = "ID сотрудника обязателен")
        Long employeeId,

        @Schema(description = "ID задачи", example = "5", required = true)
        @NotNull(message = "ID задачи обязателен")
        Long taskId,

        @Schema(description = "Время начала работы (ISO формат)", example = "2024-06-01T09:00:00", required = true)
        @NotNull(message = "Время начала обязательно")
        @PastOrPresent(message = "Время начала не может быть в будущем")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime startTime,

        @Schema(description = "Время окончания работы (ISO формат)", example = "2024-06-01T18:00:00", required = true)
        @NotNull(message = "Время окончания обязательно")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime endTime,

        @Schema(description = "Описание выполненных работ", example = "Разработал REST API для учёта времени", maxLength = 500)
        @Size(max = 500, message = "Описание работ не может превышать 500 символов")
        String description
) {
}