package com.bt1.qltv1.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = PhoneValidator.class)
@Target({ ElementType.FIELD ,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Phone {
    String message() default "{user.phone.invalid}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
