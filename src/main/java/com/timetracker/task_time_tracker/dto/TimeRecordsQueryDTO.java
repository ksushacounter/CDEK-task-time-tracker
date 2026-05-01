package com.timetracker.task_time_tracker.dto;

import com.timetracker.task_time_tracker.validation.ValidTimeRange;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDateTime;

@ValidTimeRange
public record TimeRecordsQueryDTO(

        @NotNull(message = "ID сотрудника обязателен")
        Long employeeId,

        @NotNull(message = "Дата начала периода обязательна")
        @PastOrPresent(message = "Дата начала не может быть в будущем")
        LocalDateTime startDate,

        @NotNull(message = "Дата окончания периода обязательна")
        @PastOrPresent(message = "Дата окончания не может быть в будущем")
        LocalDateTime endDate
) {
}