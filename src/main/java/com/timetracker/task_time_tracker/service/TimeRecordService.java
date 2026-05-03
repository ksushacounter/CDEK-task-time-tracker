package com.timetracker.task_time_tracker.service;

import com.timetracker.task_time_tracker.dto.TimeRecordCreateDTO;
import com.timetracker.task_time_tracker.dto.TimeRecordResponse;
import com.timetracker.task_time_tracker.exсeption.EntityNotFoundException;
import com.timetracker.task_time_tracker.mapper.DtoEntityMapper;
import com.timetracker.task_time_tracker.mapper.TaskMapper;
import com.timetracker.task_time_tracker.mapper.TimeRecordMapper;
import com.timetracker.task_time_tracker.mapper.UserMapper;
import com.timetracker.task_time_tracker.model.Task;
import com.timetracker.task_time_tracker.model.TimeRecord;
import com.timetracker.task_time_tracker.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class TimeRecordService {

    private final TimeRecordMapper timeRecordMapper;
    private final DtoEntityMapper dtoEntityMapper;
    private final TaskMapper taskMapper;
    private final UserMapper userMapper;


    public TimeRecordService(TimeRecordMapper timeRecordMapper, DtoEntityMapper dtoEntityMapper, TaskMapper taskMapper, UserMapper userMapper) {
        this.timeRecordMapper = timeRecordMapper;
        this.dtoEntityMapper = dtoEntityMapper;
        this.taskMapper = taskMapper;
        this.userMapper = userMapper;
    }

    public TimeRecordResponse create(TimeRecordCreateDTO dto) {
        Task task = taskMapper.findById(dto.taskId())
                .orElseThrow(() -> new EntityNotFoundException("Task", dto.taskId()));
        TimeRecord timeRecord = dtoEntityMapper.toEntity(dto);
        timeRecordMapper.insert(timeRecord);
        return dtoEntityMapper.toResponse(timeRecord);
    }

    public List<TimeRecordResponse> findByEmployeeAndPeriod(Long employeeId, LocalDateTime startTime, LocalDateTime endTime) {
        Optional<User> user = userMapper.findById(employeeId);
        List<TimeRecord> records = timeRecordMapper.findByEmployeeAndPeriod(employeeId, startTime, endTime);
        return records.stream()
                .map(dtoEntityMapper::toResponse)
                .toList();
    }
}
