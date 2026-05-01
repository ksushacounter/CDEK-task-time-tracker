package com.timetracker.task_time_tracker.controller;

import com.timetracker.task_time_tracker.dto.TimeRecordCreateDTO;
import com.timetracker.task_time_tracker.dto.TimeRecordResponse;
import com.timetracker.task_time_tracker.service.TimeRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/time-records")
@Tag(name = "Time Records", description = "Учёт затраченного времени на задачи")
@SecurityRequirement(name = "Bearer Authentication")
public class TimeRecordController {

    private final TimeRecordService timeRecordService;

    public TimeRecordController(TimeRecordService timeRecordService) {
        this.timeRecordService = timeRecordService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Создать запись о затраченном времени",
            description = "Фиксирует время, которое сотрудник потратил на выполнение задачи"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Запись успешно создана"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации (время начала позже окончания или неверный формат)"),
            @ApiResponse(responseCode = "401", description = "Не аутентифицирован"),
            @ApiResponse(responseCode = "404", description = "Задача с указанным ID не найдена")
    })
    public TimeRecordResponse create(
            @Parameter(description = "Данные для создания записи", required = true,
                    examples = {
                            @ExampleObject(name = "Полный рабочий день", value = """
                                    {
                                        "employeeId": 1,
                                        "taskId": 1,
                                        "startTime": "2024-06-01T09:00:00",
                                        "endTime": "2024-06-01T18:00:00",
                                        "description": "Разработка API контроллеров"
                                    }"""),
                            @ExampleObject(name = "Частичная работа", value = """
                                    {
                                        "employeeId": 1,
                                        "taskId": 2,
                                        "startTime": "2024-06-01T14:00:00",
                                        "endTime": "2024-06-01T17:30:00",
                                        "description": "Написание тестов"
                                    }""")
                    })
            @Valid @RequestBody TimeRecordCreateDTO dto) {
        return timeRecordService.create(dto);
    }

    @GetMapping(value = "/employee/{employeeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Получить записи сотрудника за период",
            description = "Возвращает все записи о времени конкретного сотрудника в указанном временном диапазоне"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список записей (может быть пустым)"),
            @ApiResponse(responseCode = "400", description = "Неверный формат даты или период"),
            @ApiResponse(responseCode = "401", description = "Не аутентифицирован")
    })
    public List<TimeRecordResponse> getByEmployeeAndPeriod(
            @Parameter(description = "ID сотрудника", example = "1", required = true)
            @PathVariable Long employeeId,

            @Parameter(description = "Начало периода (ISO формат)", example = "2024-06-01T00:00:00", required = true)
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,

            @Parameter(description = "Конец периода (ISO формат)", example = "2024-06-30T23:59:59", required = true)
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate) {

        return timeRecordService.findByEmployeeAndPeriod(employeeId, startDate, endDate);
    }
}