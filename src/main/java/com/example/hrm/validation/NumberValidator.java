package com.example.hrm.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class NumberValidator implements ConstraintValidator<ValidNumber, String> {
    private static final String PATTERN = "^[0-9]*$";

    @Override
    public void initialize(ValidNumber constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String string, ConstraintValidatorContext constraintValidatorContext) {
        if (string == null) {
            return true; // Consider using @NotNull for null checks
        }

        try {
            return Pattern.matches(PATTERN, string);
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
