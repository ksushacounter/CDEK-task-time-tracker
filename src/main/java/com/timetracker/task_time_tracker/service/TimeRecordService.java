package com.timetracker.task_time_tracker.service;

import com.timetracker.task_time_tracker.dto.TimeRecordCreateDTO;
import com.timetracker.task_time_tracker.dto.TimeRecordResponse;
import com.timetracker.task_time_tracker.mapper.DtoEntityMapper;
import com.timetracker.task_time_tracker.mapper.TimeRecordMapper;
import com.timetracker.task_time_tracker.model.TimeRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@Transactional
public class TimeRecordService {

    private final TimeRecordMapper timeRecordMapper;
    private final DtoEntityMapper dtoEntityMapper;

    public TimeRecordService(TimeRecordMapper timeRecordMapper, DtoEntityMapper dtoEntityMapper) {
        this.timeRecordMapper = timeRecordMapper;
        this.dtoEntityMapper = dtoEntityMapper;
    }

    public TimeRecordResponse create(TimeRecordCreateDTO dto) {
        TimeRecord timeRecord = dtoEntityMapper.toEntity(dto);
        timeRecordMapper.insert(timeRecord);
        return dtoEntityMapper.toResponse(timeRecord);
    }

    public List<TimeRecordResponse> findByEmployeeAndPeriod(Long employeeId, LocalDateTime startTime, LocalDateTime endTime) {
        List<TimeRecord> records = timeRecordMapper.findByEmployeeAndPeriod(employeeId, startTime, endTime);
        return records.stream()
                .map(dtoEntityMapper::toResponse)
                .toList();
    }
}
