package com.timetracker.task_time_tracker.mapper;

import com.timetracker.task_time_tracker.BaseIntegrationTest;
import com.timetracker.task_time_tracker.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class TaskMapperIT extends BaseIntegrationTest {

    @Autowired
    private TaskMapper taskMapper;

    private Task testTask;

    @BeforeEach
    void setUp() {
        testTask = new Task(null, "Интеграционная задача", "Тестирование с реальной PostgreSQL", "NEW");
    }

    @Test
    void insert_ShouldGenerateId() {
        int rows = taskMapper.insert(testTask);

        assertThat(rows).isEqualTo(1);
        assertThat(testTask.getId()).isNotNull();
    }

    @Test
    void findById_ShouldReturnTask() {
        taskMapper.insert(testTask);

        Optional<Task> found = taskMapper.findById(testTask.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Интеграционная задача");
        assertThat(found.get().getStatus()).isEqualTo("NEW");
    }

    @Test
    void updateStatus_ShouldChangeStatus() {
        taskMapper.insert(testTask);

        int rows = taskMapper.updateStatus(testTask.getId(), "IN_PROGRESS");

        assertThat(rows).isEqualTo(1);
        Optional<Task> updated = taskMapper.findById(testTask.getId());
        assertThat(updated.get().getStatus()).isEqualTo("IN_PROGRESS");
    }

    @Test
    void findAll_ShouldReturnAllTasks() {
        taskMapper.insert(testTask);
        Task secondTask = new Task(null, "Вторая задача", null, "NEW");
        taskMapper.insert(secondTask);

        List<Task> tasks = taskMapper.findAll();

        assertThat(tasks).hasSizeGreaterThanOrEqualTo(2);
        assertThat(tasks).extracting(Task::getName)
                .contains("Интеграционная задача", "Вторая задача");
    }
}