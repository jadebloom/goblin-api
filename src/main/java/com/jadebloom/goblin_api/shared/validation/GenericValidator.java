package com.jadebloom.goblin_api.shared.validation;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

/**
 * 
 * First isValid must be used to check of target is valid or not.
 * 
 * Then a if isValid returns true, getValidationErrorMessage is used to get an
 * error message.
 * 
 * Again, the preemptive usage of isValid is very crucial.
 * 
 */
public class GenericValidator {

    private static final Validator validator;

    static {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    public static <T> boolean isValid(T target) {
        Set<ConstraintViolation<T>> violations = validator.validate(target);

        return violations.isEmpty();
    }

    public static <T> String getValidationErrorMessage(T target) {
        Set<ConstraintViolation<T>> violations = validator.validate(target);

        if (violations.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();

        violations.forEach(v -> sb.append(v.getMessage() + ". "));

        return sb.toString().trim();
    }

}
