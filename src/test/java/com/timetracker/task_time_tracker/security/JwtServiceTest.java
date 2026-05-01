package com.timetracker.task_time_tracker.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    @Test
    void generateToken_ShouldCreateValidToken() {
        String token = jwtService.generateToken("testuser");

        assertThat(token).isNotNull();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        String token = jwtService.generateToken("testuser");
        String username = jwtService.extractUsername(token);

        assertThat(username).isEqualTo("testuser");
    }

    @Test
    void isTokenValid_ValidToken_ShouldReturnTrue() {
        String token = jwtService.generateToken("testuser");

        assertThat(jwtService.isTokenValid(token, "testuser")).isTrue();
    }

    @Test
    void isTokenValid_WrongUsername_ShouldReturnFalse() {
        String token = jwtService.generateToken("testuser");

        assertThat(jwtService.isTokenValid(token, "wronguser")).isFalse();
    }
}