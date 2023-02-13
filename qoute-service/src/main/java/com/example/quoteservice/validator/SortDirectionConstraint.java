package com.example.quoteservice.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {SortDirectionValidator.class})
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SortDirectionConstraint {

    String message() default "{general.validation.sortDirection}";
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
