package com.booker.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StrongPasswordValidator.class)
@Target({
  ElementType.FIELD, ElementType.PARAMETER
})
@Retention(RetentionPolicy.RUNTIME)
public @interface StrongPassword {

  String message() default "A senha deve conter pelo menos 8 caracteres, uma letra maiúscula, uma letra minúscula, um dígito e um caractere especial";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  int minLength() default 8;

  boolean requireUppercase() default true;

  boolean requireLowercase() default true;

  boolean requireDigit() default true;

  boolean requireSpecialChar() default true;
}
