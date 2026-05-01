package com.timetracker.task_time_tracker.mapper;


import com.timetracker.task_time_tracker.dto.TaskCreateDTO;
import com.timetracker.task_time_tracker.dto.TaskResponse;
import com.timetracker.task_time_tracker.dto.TimeRecordCreateDTO;
import com.timetracker.task_time_tracker.dto.TimeRecordResponse;
import com.timetracker.task_time_tracker.model.Task;
import com.timetracker.task_time_tracker.model.TimeRecord;
import org.springframework.stereotype.Component;

@Component
public class DtoEntityMapper {

    public Task toEntity(TaskCreateDTO dto) {
        return new Task(
                null,
                dto.name(),
                dto.description(),
                dto.status().getValue()
        );
    }

    public TimeRecord toEntity(TimeRecordCreateDTO dto) {
        return new TimeRecord(
                null,
                dto.employeeId(),
                dto.taskId(),
                dto.startTime(),
                dto.endTime(),
                dto.description()
        );
    }

    public TaskResponse toResponse(Task task) {
        return TaskResponse.fromEntity(task);
    }

    public TimeRecordResponse toResponse(TimeRecord record) {
        return TimeRecordResponse.fromEntity(record);
    }
}