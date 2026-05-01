package com.timetracker.task_time_tracker.mapper;

import com.timetracker.task_time_tracker.BaseIntegrationTest;
import com.timetracker.task_time_tracker.model.Task;
import com.timetracker.task_time_tracker.model.TimeRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class TimeRecordMapperIT extends BaseIntegrationTest {

    @Autowired
    private TimeRecordMapper timeRecordMapper;

    @Autowired
    private TaskMapper taskMapper;

    private Long taskId;

    @BeforeEach
    void setUp() {
        Task task = new Task(null, "Задача для TimeRecord", "Описание", "NEW");
        taskMapper.insert(task);
        taskId = task.getId();
    }

    @Test
    void insert_ShouldGenerateId() {
        TimeRecord record = new TimeRecord(null, 1L, taskId,
                LocalDateTime.of(2024, 6, 1, 9, 0),
                LocalDateTime.of(2024, 6, 1, 12, 0),
                "Работа над интеграцией");

        int rows = timeRecordMapper.insert(record);

        assertThat(rows).isEqualTo(1);
        assertThat(record.getId()).isNotNull();
    }

    @Test
    void findByEmployeeAndPeriod_ShouldReturnRecords() {
        TimeRecord record1 = new TimeRecord(null, 1L, taskId,
                LocalDateTime.of(2024, 6, 1, 9, 0),
                LocalDateTime.of(2024, 6, 1, 12, 0),
                "Утренняя работа");
        TimeRecord record2 = new TimeRecord(null, 1L, taskId,
                LocalDateTime.of(2024, 6, 2, 14, 0),
                LocalDateTime.of(2024, 6, 2, 17, 0),
                "Дневная работа");

        timeRecordMapper.insert(record1);
        timeRecordMapper.insert(record2);

        List<TimeRecord> records = timeRecordMapper.findByEmployeeAndPeriod(
                1L,
                LocalDateTime.of(2024, 6, 1, 0, 0),
                LocalDateTime.of(2024, 6, 3, 23, 59)
        );

        assertThat(records).hasSize(2);
        assertThat(records).extracting(TimeRecord::getDescription)
                .containsExactlyInAnyOrder("Утренняя работа", "Дневная работа");
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
}