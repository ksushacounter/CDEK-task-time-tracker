package com.timetracker.task_time_tracker.validation;

import com.timetracker.task_time_tracker.dto.TimeRecordCreateDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TimeRangeValidator implements ConstraintValidator<ValidTimeRange, TimeRecordCreateDTO> {

    @Override
    public boolean isValid(TimeRecordCreateDTO dto, ConstraintValidatorContext context) {
        if (dto.startTime() == null || dto.endTime() == null) {
            return true;
        }

        boolean isValid = dto.startTime().isBefore(dto.endTime());

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Время окончания должно быть позже времени начала")
                    .addPropertyNode("endTime")
                    .addConstraintViolation();
        }
        return isValid;
    }
}