package com.timetracker.task_time_tracker.mapper;

import com.timetracker.task_time_tracker.model.Task;
import com.timetracker.task_time_tracker.model.TimeRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TimeRecordMapperTest {

    @Autowired
    private TimeRecordMapper timeRecordMapper;

    @Autowired
    private TaskMapper taskMapper;

    private Long taskId;

    @BeforeEach
    void setUp() {
        timeRecordMapper.deleteAll();
        taskMapper.deleteAll();

        Task task = new Task(null, "Task для TimeRecord", "Описание", "NEW");
        taskMapper.insert(task);
        taskId = task.getId();
    }

    @Test
    void insert_ShouldGenerateId() {
        TimeRecord record = new TimeRecord(null, 1L, taskId,
                LocalDateTime.of(2024, 6, 1, 9, 0),
                LocalDateTime.of(2024, 6, 1, 12, 0),
                "Работа");

        int rows = timeRecordMapper.insert(record);

        assertThat(rows).isEqualTo(1);
        assertThat(record.getId()).isNotNull();
    }

    @Test
    void findByEmployeeAndPeriod_ShouldReturnRecords() {
        TimeRecord record1 = new TimeRecord(null, 1L, taskId,
                LocalDateTime.of(2024, 6, 1, 9, 0),
                LocalDateTime.of(2024, 6, 1, 12, 0),
                "Утро");

        TimeRecord record2 = new TimeRecord(null, 1L, taskId,
                LocalDateTime.of(2024, 6, 2, 14, 0),
                LocalDateTime.of(2024, 6, 2, 17, 0),
                "День");

        timeRecordMapper.insert(record1);
        timeRecordMapper.insert(record2);

        List<TimeRecord> records = timeRecordMapper.findByEmployeeAndPeriod(
                1L,
                LocalDateTime.of(2024, 6, 1, 0, 0),
                LocalDateTime.of(2024, 6, 3, 23, 59)
        );

        assertThat(records).hasSize(2);
        assertThat(records).extracting(TimeRecord::getDescription)
                .containsExactlyInAnyOrder("Утро", "День");
    }

    @Test
    void findByEmployeeAndPeriod_EmptyPeriod_ShouldReturnEmpty() {
        List<TimeRecord> records = timeRecordMapper.findByEmployeeAndPeriod(
                1L,
                LocalDateTime.of(2020, 1, 1, 0, 0),
                LocalDateTime.of(2020, 12, 31, 23, 59)
        );

        assertThat(records).isEmpty();
    }

    @Test
    void findByEmployeeAndPeriod_OtherEmployee_ShouldNotReturn() {
        TimeRecord record = new TimeRecord(null, 1L, taskId,
                LocalDateTime.of(2024, 6, 1, 9, 0),
                LocalDateTime.of(2024, 6, 1, 12, 0),
                "Для employee 1");

        timeRecordMapper.insert(record);

        List<TimeRecord> records = timeRecordMapper.findByEmployeeAndPeriod(
                2L,
                LocalDateTime.of(2024, 6, 1, 0, 0),
                LocalDateTime.of(2024, 6, 30, 23, 59)
        );

        assertThat(records).isEmpty();
    }
}