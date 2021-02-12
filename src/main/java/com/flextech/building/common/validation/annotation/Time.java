package com.flextech.building.common.validation.annotation;


import com.flextech.building.common.validation.TimeConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TimeConstraintValidator.class)
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Time {
    String message() default "Invalid Time";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String format() default "HH:mm";
}
