package com.jadebloom.goblin_api.currency.util;

import java.util.Set;

import com.jadebloom.goblin_api.currency.dto.CurrencyDto;
import com.jadebloom.goblin_api.currency.error.InvalidCurrencyException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

public class CurrencyValidators {

    private static final Validator validator;

    static {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    public static void validate(CurrencyDto currencyDto) {
        Set<ConstraintViolation<CurrencyDto>> violations = validator.validate(currencyDto);

        if (violations.isEmpty()) {
            return;
        }

        StringBuilder sb = new StringBuilder();

        violations.forEach(v -> sb.append(v.getMessage()));

        throw new InvalidCurrencyException(sb.toString());
    }

}
