package com.jadebloom.goblin_api.currency.validation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.hibernate.validator.constraints.ConstraintComposition;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.Pattern;

@Pattern(regexp = "[A-Z][A-Z][A-Z]")
@ReportAsSingleViolation
@Target({ FIELD, METHOD, PARAMETER, ANNOTATION_TYPE, TYPE_USE })
@Retention(RUNTIME)
@ConstraintComposition
@Constraint(validatedBy = {})
public @interface ValidCurrencyAlphabeticalCode {

    String message() default "The currency's optional alphabetical code must conform to ISO 4217";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
