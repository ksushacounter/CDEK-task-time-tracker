package com.timetracker.task_time_tracker.validation;

import com.timetracker.task_time_tracker.dto.TimeRecordCreateDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TimeRangeValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validTimeRange_ShouldPass() {
        TimeRecordCreateDTO dto = new TimeRecordCreateDTO(
                1L, 1L,
                LocalDateTime.of(2024, 6, 1, 9, 0),
                LocalDateTime.of(2024, 6, 1, 17, 0),
                "Рабочий день"
        );

        Set<ConstraintViolation<TimeRecordCreateDTO>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void invalidTimeRange_EndBeforeStart_ShouldFail() {
        TimeRecordCreateDTO dto = new TimeRecordCreateDTO(
                1L, 1L,
                LocalDateTime.of(2024, 6, 1, 17, 0),
                LocalDateTime.of(2024, 6, 1, 9, 0),
                "Неверный интервал"
        );

        Set<ConstraintViolation<TimeRecordCreateDTO>> violations = validator.validate(dto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage())
                .contains("Время окончания должно быть позже времени начала");
    }
}