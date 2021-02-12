package com.flextech.building.common.validation;


import com.flextech.building.common.validation.annotation.DateTime;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeConstraintValidator implements ConstraintValidator<DateTime, String> {
    private DateTimeFormatter dateTimeFormatter;

    @Override
    public void initialize(DateTime constraintAnnotation) {
        dateTimeFormatter = DateTimeFormatter.ofPattern(constraintAnnotation.format());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            LocalDateTime.parse(value, dateTimeFormatter);
            return true;
        } catch (Exception e) {

        }
        return false;
    }
}
