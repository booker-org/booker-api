package com.booker.validators;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = StrongPasswordValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface StrongPassword {
  String message() default "The password must contain at least 8 characters, one uppercase letter, one lowercase letter, one digit, and one special character";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  int minLength() default 8;

  boolean requireUppercase() default true;

  boolean requireLowercase() default true;

  boolean requireDigit() default true;

  boolean requireSpecialChar() default true;
}