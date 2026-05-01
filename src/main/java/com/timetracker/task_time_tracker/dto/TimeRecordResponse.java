package com.timetracker.task_time_tracker.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.timetracker.task_time_tracker.model.TimeRecord;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Ответ с данными о затраченном времени")
public record TimeRecordResponse(

        @Schema(description = "Уникальный идентификатор записи", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        @Schema(description = "ID сотрудника", example = "1")
        Long employeeId,

        @Schema(description = "ID задачи", example = "5")
        Long taskId,

        @Schema(description = "Время начала работы", example = "2024-06-01T09:00:00")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime startTime,

        @Schema(description = "Время окончания работы", example = "2024-06-01T18:00:00")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime endTime,

        @Schema(description = "Описание выполненных работ", example = "Разработал REST API")
        String description,

        @Schema(description = "Продолжительность работы в минутах", example = "540")
        long durationMinutes
) {
    public static TimeRecordResponse fromEntity(TimeRecord record) {
        long minutes = java.time.Duration.between(record.getStartTime(), record.getEndTime()).toMinutes();
        return new TimeRecordResponse(
                record.getId(),
                record.getEmployeeId(),
                record.getTaskId(),
                record.getStartTime(),
                record.getEndTime(),
                record.getDescription(),
                minutes
        );
    }
}