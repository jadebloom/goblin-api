package com.jadebloom.goblin_api.expense.validation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.hibernate.validator.constraints.ConstraintComposition;

import com.jadebloom.goblin_api.shared.validation.NotAllWhitespace;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.Size;

@NotAllWhitespace
@Size(min = 1, max = 256)
@ReportAsSingleViolation
@Target({ FIELD, METHOD, PARAMETER, ANNOTATION_TYPE, TYPE_USE })
@Retention(RUNTIME)
@ConstraintComposition
@Constraint(validatedBy = {})
public @interface ValidExpenseCategoryDescription {

    String message() default "The expense category's optional description must be 1 - 256 characters long";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
