package com.bt1.qltv1.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {CsvValidator.class})
public @interface CsvType {
    String message() default "Just support CSV file!!!";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
