package com.jadebloom.goblin_api.shared.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotAllWhitespaceValidator implements ConstraintValidator<NotAllWhitespace, String> {

	@Override
	public void initialize(NotAllWhitespace constraintAnnotation) {
		ConstraintValidator.super.initialize(constraintAnnotation);
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null || value.isEmpty()) {
			return true;
		}

		return !value.trim().isEmpty();
	}

}
