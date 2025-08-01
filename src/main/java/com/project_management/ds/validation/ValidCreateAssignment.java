package com.project_management.ds.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CreateAssignment.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCreateAssignment {
    Class<? extends Enum<?>> enumClass();
    String message() default "Valor inválido. Deve ser um dos valores permitidos: {enumClass}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
