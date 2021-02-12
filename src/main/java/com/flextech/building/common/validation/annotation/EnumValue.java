package com.flextech.building.common.validation.annotation;

import com.flextech.building.common.validation.EnumValueValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = EnumValueValidator.class)
public @interface EnumValue {
    public abstract Class<? extends Enum<?>> enumClass();
    String message() default "Invalid enum svalue.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
