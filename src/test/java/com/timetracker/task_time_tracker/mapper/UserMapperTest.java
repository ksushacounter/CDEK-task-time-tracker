package com.timetracker.task_time_tracker.mapper;

import com.timetracker.task_time_tracker.model.User;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    @Sql(scripts = "/schema.sql")
    void findByUsername_ShouldReturnUser() {
        User user = userMapper.findByUsername("admin");

        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("admin");
        assertThat(user.getRole()).isEqualTo("ADMIN");
        assertThat(user.isEnabled()).isTrue();
    }

    @Test
    void findByUsername_NotFound_ShouldReturnNull() {
        User user = userMapper.findByUsername("nonexistent");

        assertThat(user).isNull();
    }
}