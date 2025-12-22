package com.jadebloom.goblin_api.security.validation;

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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@NotBlank
@Size(min = 8, max = 32)
@ReportAsSingleViolation
@Target({ FIELD, METHOD, PARAMETER, ANNOTATION_TYPE, TYPE_USE })
@Retention(RUNTIME)
@ConstraintComposition
@Constraint(validatedBy = { UserPasswordValidator.class })
public @interface ValidUserPassword {

    String message() default "The user's password must be 8 - 32 characters long and contain at least one uppercase letter, number and special symbol ('_', '!')";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
