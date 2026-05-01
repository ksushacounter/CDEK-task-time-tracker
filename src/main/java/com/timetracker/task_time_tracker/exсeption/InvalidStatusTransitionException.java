package com.timetracker.task_time_tracker.exсeption;

public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(String from, String to) {
        super(String.format("Cannot transition status from '%s' to '%s'", from, to));
    }
}