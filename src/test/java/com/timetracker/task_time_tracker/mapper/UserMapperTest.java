package com.timetracker.task_time_tracker.mapper;

import com.timetracker.task_time_tracker.model.User;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    @Sql(scripts = "/schema.sql")
    void findByUsername_ShouldReturnUser() {
        Optional<User> userOpt = userMapper.findByUsername("admin");

        assertThat(userOpt).isPresent();
        assertThat(userOpt.get().getUsername()).isEqualTo("admin");
        assertThat(userOpt.get().getRole()).isEqualTo("ADMIN");
        assertThat(userOpt.get().isEnabled()).isTrue();
    }

    @Test
    void findByUsername_NotFound_ShouldReturnEmpty() {
        Optional<User> userOpt = userMapper.findByUsername("nonexistent");

        assertThat(userOpt).isEmpty();
    }

    @Test
    void findById_ShouldReturnUser() {
        Optional<User> userOpt = userMapper.findById(1L);
        assertThat(userOpt).isPresent();
    }

    @Test
    void findById_NotFound_ShouldReturnEmpty() {
        Optional<User> userOpt = userMapper.findById(99999L);

        assertThat(userOpt).isEmpty();
    }
}