package com.flextech.building.common.validation;


import com.flextech.building.common.validation.annotation.EnumValue;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EnumValueValidator implements ConstraintValidator<EnumValue, Object> {

    private Object[] enumValues;

    @Override
    public void initialize(final EnumValue annotation)
    {
        enumValues = annotation.enumClass().getEnumConstants();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context)
    {
        if (null != value) {
            String contextValue = value.toString();
            for (Object enumValue : enumValues) {
                if (enumValue.toString().equals(contextValue)) {
                    return true;
                }
            }
        }

        return false;
    }
}
