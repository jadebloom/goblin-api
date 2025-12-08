package com.jadebloom.goblin_api.expense.validation;

import java.util.Set;

import com.jadebloom.goblin_api.expense.dto.ExpenseCategoryDto;
import com.jadebloom.goblin_api.expense.error.InvalidExpenseCategoryException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

public class ExpenseCategoryValidators {

    private static final Validator validator;

    static {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    public static void validate(ExpenseCategoryDto expenseCategoryDto) {
        Set<ConstraintViolation<ExpenseCategoryDto>> violations = validator.validate(
                expenseCategoryDto);

        if (violations.isEmpty()) {
            return;
        }

        StringBuilder sb = new StringBuilder();

        violations.forEach(v -> sb.append(v.getMessage() + ". "));

        throw new InvalidExpenseCategoryException(sb.toString().trim());
    }

}
