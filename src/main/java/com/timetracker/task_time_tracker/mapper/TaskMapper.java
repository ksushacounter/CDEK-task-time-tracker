package com.timetracker.task_time_tracker.mapper;

import com.timetracker.task_time_tracker.model.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface TaskMapper {
    int insert(Task task);

    Optional<Task> findById(Long id);

    int updateStatus(@Param("id") Long id, @Param("status") String status);

    List<Task> findAll();

    void deleteAll();
}