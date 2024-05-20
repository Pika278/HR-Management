package com.example.hrm.validation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalTimeValidator implements ConstraintValidator<ValidLocalTime, LocalTime> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public void initialize(ValidLocalTime constraintAnnotation) {
    }

    @Override
    public boolean isValid(LocalTime value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Consider using @NotNull for null checks
        }

        try {
            String formattedTime = value.format(FORMATTER);
            LocalTime.parse(formattedTime, FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
