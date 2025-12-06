package com.jadebloom.goblin_api.currency.util;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.jadebloom.goblin_api.currency.dto.InvalidCurrencyException;
import com.jadebloom.goblin_api.currency.entity.CurrencyEntity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@Component
public class CurrencyEntityValidator {

    private static Validator validator;

    private static String DEFAULT_VIOLATION_MESSAGE = "Currency's \"%s\" is invalid";

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

        validator = validatorFactory.getValidator();
    }

    public static void validateProperty(CurrencyEntity currencyEntity, String propertyName) {
        Set<ConstraintViolation<CurrencyEntity>> constraintViolations = validator.validateProperty(
                currencyEntity,
                propertyName);

        if (!constraintViolations.isEmpty()) {
            String message = constraintViolations.iterator().next().getMessage();

            throw new InvalidCurrencyException(
                    message.isBlank() ? DEFAULT_VIOLATION_MESSAGE : message);
        }
    }

}
