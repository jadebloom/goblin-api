package com.jadebloom.goblin_api.security.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UserPasswordValidator implements ConstraintValidator<ValidUserPassword, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean hasUppercaseLetter = false;
        boolean hasNumber = false;
        boolean hasSpecialSymbol = false;

        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);

            if ('A' <= c && c <= 'Z') {
                hasUppercaseLetter = true;
            }

            if ('0' <= c && c <= '9') {
                hasNumber = true;
            }

            if (c == '_' || c == '!') {
                hasSpecialSymbol = true;
            }
        }

        return hasUppercaseLetter && hasNumber && hasSpecialSymbol;
    }

}
