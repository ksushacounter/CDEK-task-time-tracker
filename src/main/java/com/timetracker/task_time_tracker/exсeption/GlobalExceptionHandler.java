package com.timetracker.task_time_tracker.exсeption;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleNotFound(EntityNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(InvalidStatusTransitionException.class)
    public ProblemDetail handleInvalidTransition(InvalidStatusTransitionException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(InvalidTaskStatusException.class)
    public ProblemDetail handleInvalidTaskStatus(InvalidTaskStatusException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setProperty("timestamp", LocalDateTime.now());
        problem.setProperty("invalidStatus", ex.getInvalidStatus());
        problem.setProperty("allowedValues", new String[]{"NEW", "IN_PROGRESS", "DONE"});
        return problem;
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ProblemDetail handleDateTimeParse(DateTimeParseException ex) {
        String invalidDate = ex.getParsedString();
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                String.format("Неверный формат даты: '%s'. Ожидаемый формат: yyyy-MM-ddTHH:mm:ss (например, 2024-06-01T09:00:00)", invalidDate)
        );
        problem.setProperty("timestamp", LocalDateTime.now());
        problem.setProperty("invalidDate", invalidDate);
        problem.setProperty("expectedFormat", "yyyy-MM-ddTHH:mm:ss");
        return problem;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String message = "Неверный формат запроса. ";
        String errorDetail = ex.getMessage();

        if (errorDetail != null) {
            if (errorDetail.contains("TaskStatus")) {
                message = "Недопустимое значение статуса. Допустимые значения: NEW, IN_PROGRESS, DONE";
            } else if (errorDetail.contains("LocalDateTime")) {
                message = "Неверный формат даты. Ожидаемый формат: yyyy-MM-ddTHH:mm:ss";
            } else {
                message = "Неверный формат JSON или типов данных";
            }
        }

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message);
        problem.setProperty("timestamp", LocalDateTime.now());
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Ошибка валидации");
        problem.setProperty("timestamp", LocalDateTime.now());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(field, message);
        });
        problem.setProperty("errors", errors);

        return problem;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Ошибка валидации");
        problem.setProperty("timestamp", LocalDateTime.now());
        problem.setProperty("errors", ex.getConstraintViolations().stream()
                .collect(HashMap::new,
                        (m, v) -> m.put(v.getPropertyPath().toString(), v.getMessage()),
                        HashMap::putAll));
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneral(Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера");
        problem.setProperty("timestamp", LocalDateTime.now());
        problem.setProperty("exception", ex.getClass().getSimpleName());
        return problem;
    }
}