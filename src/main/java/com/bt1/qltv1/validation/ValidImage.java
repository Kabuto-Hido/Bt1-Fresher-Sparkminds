package com.bt1.qltv1.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ImageValidator.class})
public @interface ValidImage {
    String message() default "Just support JPG, PNG file!!";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
