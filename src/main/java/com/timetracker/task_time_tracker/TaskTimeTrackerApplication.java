package com.timetracker.task_time_tracker;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.timetracker.task_time_tracker.mapper")
@EnableTransactionManagement
public class TaskTimeTrackerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaskTimeTrackerApplication.class, args);
    }
}