package com.jadebloom.goblin_api.shared.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class HexColorCodeValidator implements ConstraintValidator<ValidHexColorCode, String> {

	@Override
	public void initialize(ValidHexColorCode constraintAnnotation) {
		ConstraintValidator.super.initialize(constraintAnnotation);
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}

		if (value.isBlank() || value.charAt(0) != '#') {
			return false;
		}

		if (!(value.length() == 4 || value.length() == 7)) {
			return false;
		}

		for (int i = 1; i < value.length(); i++) {
			boolean a = value.charAt(i) >= '0' && value.charAt(i) <= '9';

			boolean b = value.charAt(i) >= 'a' && value.charAt(i) <= 'f';

			boolean c = value.charAt(i) >= 'A' && value.charAt(i) <= 'F';

			if (!(a || b || c)) {
				return false;
			}
		}

		return true;
	}

}
