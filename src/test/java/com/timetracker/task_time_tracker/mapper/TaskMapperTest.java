package com.timetracker.task_time_tracker.mapper;

import com.timetracker.task_time_tracker.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaskMapperTest {

    @Autowired
    private TaskMapper taskMapper;

    private Task testTask;

    @BeforeEach
    void setUp() {
        taskMapper.deleteAll();

        testTask = new Task(null, "Тестовая задача", "Описание", "NEW");
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
        assertThat(found.get().getName()).isEqualTo("Тестовая задача");
        assertThat(found.get().getStatus()).isEqualTo("NEW");
    }

    @Test
    void findById_NotFound_ShouldReturnEmpty() {
        Optional<Task> found = taskMapper.findById(99999L);

        assertThat(found).isEmpty();
    }

    @Test
    void updateStatus_ShouldChangeStatus() {
        taskMapper.insert(testTask);

        int rows = taskMapper.updateStatus(testTask.getId(), "DONE");

        assertThat(rows).isEqualTo(1);

        Optional<Task> updated = taskMapper.findById(testTask.getId());
        assertThat(updated.get().getStatus()).isEqualTo("DONE");
    }

    @Test
    void findAll_ShouldReturnOnlyTestTasks() {
        taskMapper.insert(testTask);

        Task secondTask = new Task(null, "Вторая задача", "Описание 2", "IN_PROGRESS");
        taskMapper.insert(secondTask);

        List<Task> tasks = taskMapper.findAll();

        assertThat(tasks).hasSize(2);
        assertThat(tasks).extracting(Task::getName)
                .containsExactlyInAnyOrder("Тестовая задача", "Вторая задача");
    }
}