package com.flextech.building.common.validation;


import com.flextech.building.common.validation.annotation.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateConstraintValidator implements ConstraintValidator<Date, String> {

    private DateTimeFormatter dateTimeFormatter;

    @Override
    public void initialize(Date constraintAnnotation) {
        dateTimeFormatter = DateTimeFormatter.ofPattern(constraintAnnotation.format());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            LocalDate.parse(value, dateTimeFormatter);
            return true;
        } catch (Exception e) {

        }
        return false;
    }
}
