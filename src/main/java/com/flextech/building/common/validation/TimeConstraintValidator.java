package com.flextech.building.common.validation;


import com.flextech.building.common.validation.annotation.Time;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeConstraintValidator implements ConstraintValidator<Time, String> {

    private DateTimeFormatter dateTimeFormatter;

    @Override
    public void initialize(Time constraintAnnotation) {
        dateTimeFormatter = DateTimeFormatter.ofPattern(constraintAnnotation.format());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            LocalTime.parse(value, dateTimeFormatter);
            return true;
        } catch (Exception e) {

        }
        return false;
    }
}
