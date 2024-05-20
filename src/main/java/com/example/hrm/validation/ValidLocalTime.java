package com.example.hrm.validation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = com.example.hrm.validation.LocalTimeValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidLocalTime {
    String message() default "Invalid time format. Expected format is HH:mm:ss AM/PM.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
