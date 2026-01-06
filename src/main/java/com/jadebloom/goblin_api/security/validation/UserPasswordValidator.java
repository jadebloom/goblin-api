package com.jadebloom.goblin_api.security.validation;

import java.util.List;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UserPasswordValidator implements ConstraintValidator<ValidUserPassword, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		boolean hasUppercaseLetter = false;
		boolean hasLowercaseLetter = false;
		boolean hasNumber = false;
		boolean hasSpecialSymbol = false;

		List<Character> specialSymbols = List.of('!', '?', '_', '$', '#', '%', '^', '&', '*');

		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);

			if ('a' <= c && c <= 'z') {
				hasLowercaseLetter = true;

				continue;
			}

			if ('A' <= c && c <= 'Z') {
				hasUppercaseLetter = true;

				continue;
			}

			if ('0' <= c && c <= '9') {
				hasNumber = true;

				continue;
			}

			if (specialSymbols.contains(c)) {
				hasSpecialSymbol = true;

				continue;
			}


			return false;
		}

		return hasLowercaseLetter && hasUppercaseLetter && hasNumber && hasSpecialSymbol;
	}

}
