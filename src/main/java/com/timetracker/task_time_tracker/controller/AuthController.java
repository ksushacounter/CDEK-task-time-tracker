package com.timetracker.task_time_tracker.controller;

import com.timetracker.task_time_tracker.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Аутентификация и управление JWT токенами")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public record AuthRequest(String username, String password) {
    }

    public record AuthResponse(String token, String username, String role) {
    }

    public record ErrorResponse(String timestamp, int status, String error, String message, String path) {
    }

    @PostMapping("/login")
    @Operation(
            summary = "Аутентификация пользователя",
            description = "Проверяет логин и пароль, возвращает JWT токен для доступа к API"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная аутентификация, JWT токен получен",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "token": "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc0NjE4ODAwMCwiZXhwIjoxNzQ2Mjc0NDAwfQ.signature",
                                        "username": "admin",
                                        "role": "ADMIN"
                                    }"""))),
            @ApiResponse(responseCode = "401", description = "Неверное имя пользователя или пароль",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "timestamp": "2026-05-02T10:00:00",
                                        "status": 401,
                                        "error": "Unauthorized",
                                        "message": "Неверное имя пользователя или пароль",
                                        "path": "/auth/login"
                                    }""")))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Учётные данные пользователя",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "admin",
                            value = """
                                    {
                                        "username": "admin",
                                        "password": "admin123"
                                    }"""
                    )
            )
    )
    public ResponseEntity<?> login(
            @org.springframework.web.bind.annotation.RequestBody AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails.getUsername());

            AuthResponse response = new AuthResponse(
                    token,
                    userDetails.getUsername(),
                    userDetails.getAuthorities().iterator().next().getAuthority()
            );

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            ErrorResponse error = new ErrorResponse(
                    LocalDateTime.now().toString(),
                    401,
                    "Unauthorized",
                    "Неверное имя пользователя или пароль",
                    "/auth/login"
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Выход из системы",
            description = "Инвалидирует текущую сессию (клиент должен удалить JWT токен на своей стороне)"
    )
    @ApiResponse(responseCode = "200", description = "Успешный выход из системы")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("message", "Успешный выход из системы"));
    }
}