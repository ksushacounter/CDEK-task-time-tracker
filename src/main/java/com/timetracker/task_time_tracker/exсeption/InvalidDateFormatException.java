package com.timetracker.task_time_tracker.exсeption;

public class InvalidDateFormatException extends RuntimeException {

    private final String invalidDate;
    private final String expectedFormat;

    public InvalidDateFormatException(String invalidDate, Throwable cause) {
        super(String.format("Неверный формат даты: '%s'. Ожидаемый формат: yyyy-MM-ddTHH:mm:ss (например, 2024-06-01T09:00:00)", invalidDate), cause);
        this.invalidDate = invalidDate;
        this.expectedFormat = "yyyy-MM-ddTHH:mm:ss";
    }

    public String getInvalidDate() {
        return invalidDate;
    }

    public String getExpectedFormat() {
        return expectedFormat;
    }
}