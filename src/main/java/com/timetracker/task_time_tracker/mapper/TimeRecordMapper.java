package com.timetracker.task_time_tracker.mapper;

import com.timetracker.task_time_tracker.model.TimeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TimeRecordMapper {

    int insert(TimeRecord timeRecord);

    List<TimeRecord> findByEmployeeAndPeriod(@Param("employeeId") Long employeeId,
                                             @Param("startDate") LocalDateTime startTime,
                                             @Param("endDate") LocalDateTime endTime);

    void deleteAll();
}
