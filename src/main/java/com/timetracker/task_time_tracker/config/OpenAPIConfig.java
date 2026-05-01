package com.timetracker.task_time_tracker.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Task Time Tracker API")
                        .version("1.0.0")
                        .description("""
                                REST API для учёта рабочего времени сотрудников.
                                
                                ## Возможности:
                                * Управление задачами (CRUD)
                                * Учёт затраченного времени
                                * JWT аутентификация
                                * Статусные переходы задач (NEW → IN_PROGRESS → DONE)
                                """)
                        .contact(new Contact()
                                .name("KsushkaPushkaDev")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Development Server"),
                        new Server().url("https://api.timetracker.com").description("Production Server")
                ))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", new SecurityScheme()
                                .name("Bearer Authentication")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Введите JWT токен: Bearer <token>")));
    }
}