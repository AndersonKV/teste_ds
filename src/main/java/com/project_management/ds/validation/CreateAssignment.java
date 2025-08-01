package com.project_management.ds.validation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class CreateAssignment implements ConstraintValidator<ValidCreateAssignment, String> {

    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(ValidCreateAssignment constraintAnnotation) {
        this.enumClass = constraintAnnotation.enumClass();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;

        return Arrays.stream(enumClass.getEnumConstants())
                .anyMatch(e -> e.name().equalsIgnoreCase(value));
    }
}
