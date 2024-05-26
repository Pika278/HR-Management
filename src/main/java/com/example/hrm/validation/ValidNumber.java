package com.example.hrm.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = com.example.hrm.validation.NumberValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidNumber {
    String message() default "Chỉ nhập số";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
