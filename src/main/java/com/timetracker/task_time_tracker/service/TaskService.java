package com.timetracker.task_time_tracker.service;


import com.timetracker.task_time_tracker.dto.TaskCreateDTO;
import com.timetracker.task_time_tracker.dto.TaskResponse;
import com.timetracker.task_time_tracker.dto.TaskStatus;
import com.timetracker.task_time_tracker.dto.TaskUpdateStatusDTO;
import com.timetracker.task_time_tracker.exсeption.EntityNotFoundException;
import com.timetracker.task_time_tracker.exсeption.InvalidStatusTransitionException;
import com.timetracker.task_time_tracker.mapper.DtoEntityMapper;
import com.timetracker.task_time_tracker.mapper.TaskMapper;
import com.timetracker.task_time_tracker.model.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class TaskService {

    private final TaskMapper taskMapper;
    private final DtoEntityMapper dtoMapper;

    public TaskService(TaskMapper taskMapper, DtoEntityMapper dtoMapper) {
        this.taskMapper = taskMapper;
        this.dtoMapper = dtoMapper;
    }

    public TaskResponse create(TaskCreateDTO dto) {
        Task task = dtoMapper.toEntity(dto);
        taskMapper.insert(task);
        return dtoMapper.toResponse(task);
    }

    public TaskResponse getById(Long id) {
        return taskMapper.findById(id)
                .map(dtoMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Task", id));
    }

    public TaskResponse updateStatus(Long id, TaskUpdateStatusDTO dto) {
        Task task = taskMapper.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task", id));

        if ("DONE".equals(task.getStatus()) && dto.status() != TaskStatus.DONE) {
            throw new InvalidStatusTransitionException(task.getStatus(), dto.status().getValue());
        }

        taskMapper.updateStatus(id, dto.status().getValue());
        return taskMapper.findById(id)
                .map(dtoMapper::toResponse)
                .orElseThrow();
    }
}