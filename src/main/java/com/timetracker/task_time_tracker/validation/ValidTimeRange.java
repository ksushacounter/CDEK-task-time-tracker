package com.timetracker.task_time_tracker.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = TimeRangeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTimeRange {
    String message() default "Время окончания должно быть позже времени начала";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}