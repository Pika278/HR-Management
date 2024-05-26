package com.example.hrm.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = com.example.hrm.validation.LocalDateTimeValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidLocalDateTime {
    String message() default "  Sai định dạng thời gian";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
