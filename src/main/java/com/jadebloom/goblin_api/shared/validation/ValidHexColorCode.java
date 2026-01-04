package com.jadebloom.goblin_api.shared.validation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ FIELD, METHOD, PARAMETER, ANNOTATION_TYPE, TYPE_USE })
@Retention(RUNTIME)
@Constraint(validatedBy = HexColorCodeValidator.class)
public @interface ValidHexColorCode {

	String message() default "The hex color code must start from # and contains 3 or 6 more lowercase letters, uppercase letters, or numbers";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
