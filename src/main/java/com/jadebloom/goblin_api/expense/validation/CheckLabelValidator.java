package com.jadebloom.goblin_api.expense.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CheckLabelValidator implements ConstraintValidator<CheckLabel, String> {

    @Override
    public void initialize(CheckLabel constraintAnnotation) {
    }

    @Override
    public boolean isValid(String object, ConstraintValidatorContext constraintContext) {
        return object != null && !object.isBlank() && object.length() <= 32;
    }

}
