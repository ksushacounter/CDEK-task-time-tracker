package com.timetracker.task_time_tracker.exсeption;

public class InvalidTaskStatusException extends RuntimeException {

    private final String invalidStatus;

    public InvalidTaskStatusException(String invalidStatus) {
        super(String.format("Недопустимый статус задачи: '%s'. Допустимые значения: NEW, IN_PROGRESS, DONE", invalidStatus));
        this.invalidStatus = invalidStatus;
    }

    public String getInvalidStatus() {
        return invalidStatus;
    }
}