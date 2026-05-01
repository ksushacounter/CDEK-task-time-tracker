package com.timetracker.task_time_tracker.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TaskStatus {
    NEW("NEW"),
    IN_PROGRESS("IN_PROGRESS"),
    DONE("DONE");

    private final String value;

    TaskStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static TaskStatus fromValue(String value) {
        for (TaskStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + value);
    }
}