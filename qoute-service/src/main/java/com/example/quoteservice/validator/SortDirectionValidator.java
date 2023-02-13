package com.example.quoteservice.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

public class SortDirectionValidator implements ConstraintValidator<SortDirectionConstraint, String> {
    @Override
    public boolean isValid(String sortDirection, ConstraintValidatorContext constraintValidatorContext) {
        return Optional.ofNullable(sortDirection)
                .map(direction-> direction.equals("ASC")||direction.equals("DESC"))
                .orElse(true);
    }
}
