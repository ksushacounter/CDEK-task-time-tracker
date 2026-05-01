package com.timetracker.task_time_tracker.service;

import com.timetracker.task_time_tracker.dto.TaskCreateDTO;
import com.timetracker.task_time_tracker.dto.TaskStatus;
import com.timetracker.task_time_tracker.dto.TimeRecordCreateDTO;
import com.timetracker.task_time_tracker.dto.TimeRecordResponse;
import com.timetracker.task_time_tracker.mapper.TaskMapper;
import com.timetracker.task_time_tracker.mapper.TimeRecordMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class TimeRecordServiceTest {

    @Autowired
    private TimeRecordService timeRecordService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TimeRecordMapper timeRecordMapper;

    private Long taskId;

    @BeforeEach
    void setUp() {
        taskMapper.deleteAll();

        TaskCreateDTO taskDto = new TaskCreateDTO(
                "Task для TimeRecord",
                "Описание",
                TaskStatus.NEW
        );
        taskId = taskService.create(taskDto).id();
    }

    @Test
    void createTimeRecord_ShouldSaveAndReturn() {
        TimeRecordCreateDTO dto = new TimeRecordCreateDTO(
                1L,
                taskId,
                LocalDateTime.of(2024, 6, 1, 9, 0),
                LocalDateTime.of(2024, 6, 1, 12, 30),
                "Работа над задачей"
        );

        TimeRecordResponse response = timeRecordService.create(dto);

        assertThat(response.id()).isNotNull();
        assertThat(response.employeeId()).isEqualTo(1L);
        assertThat(response.taskId()).isEqualTo(taskId);
        assertThat(response.durationMinutes()).isEqualTo(210);
    }

    @Test
    void findByEmployeeAndPeriod_ShouldReturnRecords() {
        TimeRecordCreateDTO dto1 = new TimeRecordCreateDTO(
                1L, taskId,
                LocalDateTime.of(2024, 6, 1, 9, 0),
                LocalDateTime.of(2024, 6, 1, 12, 0),
                "Утро"
        );

        TimeRecordCreateDTO dto2 = new TimeRecordCreateDTO(
                1L, taskId,
                LocalDateTime.of(2024, 6, 2, 14, 0),
                LocalDateTime.of(2024, 6, 2, 17, 0),
                "День"
        );

        timeRecordService.create(dto1);
        timeRecordService.create(dto2);

        List<TimeRecordResponse> records = timeRecordService.findByEmployeeAndPeriod(
                1L,
                LocalDateTime.of(2024, 6, 1, 0, 0),
                LocalDateTime.of(2024, 6, 3, 23, 59)
        );

        assertThat(records).hasSize(2);
        assertThat(records).extracting(TimeRecordResponse::description)
                .containsExactlyInAnyOrder("Утро", "День");
    }

    @Test
    void findByEmployeeAndPeriod_EmptyPeriod_ShouldReturnEmptyList() {
        List<TimeRecordResponse> records = timeRecordService.findByEmployeeAndPeriod(
                1L,
                LocalDateTime.of(2020, 1, 1, 0, 0),
                LocalDateTime.of(2020, 1, 31, 23, 59)
        );

        assertThat(records).isEmpty();
    }
}