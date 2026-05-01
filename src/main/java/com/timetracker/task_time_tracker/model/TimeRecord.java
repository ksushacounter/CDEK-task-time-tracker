package com.timetracker.task_time_tracker.model;

import java.time.LocalDateTime;

public class TimeRecord {
    private Long id;
    private Long employeeId;
    private Long taskId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String description;

    public TimeRecord() {
    }

    public TimeRecord(Long id, Long employeeId, Long taskId,
                      LocalDateTime startTime, LocalDateTime endTime, String description) {
        this.id = id;
        this.employeeId = employeeId;
        this.taskId = taskId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}