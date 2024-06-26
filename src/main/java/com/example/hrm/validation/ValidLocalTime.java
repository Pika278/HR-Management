package com.example.hrm.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = com.example.hrm.validation.LocalTimeValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidLocalTime {
    String message() default "Sai định dạng thời gian. Định dạng đúng là HH:mm:ss AM/PM.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
