package com.timetracker.task_time_tracker.mapper;

import com.timetracker.task_time_tracker.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface UserMapper {

    Optional<User> findByUsername(@Param("username") String username);

    Optional<User> findById(@Param("id") Long id);
}