package com.timetracker.task_time_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Имя пользователя обязательно")
        @Size(min = 3, max = 50, message = "Имя пользователя должно быть от 3 до 50 символов")
        String username,

        @NotBlank(message = "Пароль обязателен")
        @Size(min = 4, max = 100, message = "Пароль должен быть от 4 до 100 символов")
        String password
) {
}