package com.jadebloom.goblin_api.expense.validation;

import java.util.Set;

import com.jadebloom.goblin_api.expense.error.InvalidExpenseCategoryException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

public class ExpenseCategoryValidators {

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

        throw new InvalidExpenseCategoryException(sb.toString().trim());
    }

}
