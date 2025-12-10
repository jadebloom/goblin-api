package com.jadebloom.goblin_api.currency.validation;

import java.util.Set;

import com.jadebloom.goblin_api.currency.error.InvalidCurrencyException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

public class CurrencyValidators {

    private static final Validator validator;

    static {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    public static <T> void validate(T target) {
        Set<ConstraintViolation<T>> violations = validator.validate(target);

        if (violations.isEmpty()) {
            return;
        }

        StringBuilder sb = new StringBuilder();

        violations.forEach(v -> sb.append(v.getMessage() + ". "));

        throw new InvalidCurrencyException(sb.toString().trim());
    }

}
