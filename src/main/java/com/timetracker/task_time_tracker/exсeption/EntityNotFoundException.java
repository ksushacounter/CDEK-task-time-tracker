package com.timetracker.task_time_tracker.exсeption;

public class EntityNotFoundException extends RuntimeException {

    private final String entityType;
    private final Long entityId;

    public EntityNotFoundException(String entityType, Long entityId) {
        super(String.format("%s with id %d not found", entityType, entityId));
        this.entityType = entityType;
        this.entityId = entityId;
    }

    public String getEntityType() {
        return entityType;
    }

    public Long getEntityId() {
        return entityId;
    }
}