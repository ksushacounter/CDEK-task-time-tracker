package com.timetracker.task_time_tracker.dto;

import jakarta.validation.constraints.NotNull;

public record TaskUpdateStatusDTO(
        @NotNull(message = "Новый статус обязателен")
        TaskStatus status
) {}