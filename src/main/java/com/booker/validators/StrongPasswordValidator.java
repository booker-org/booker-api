package com.booker.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

  private int minLength;
  private boolean requireUppercase;
  private boolean requireLowercase;
  private boolean requireDigit;
  private boolean requireSpecialChar;

  @Override
  public void initialize(StrongPassword constraintAnnotation) {
    this.minLength = constraintAnnotation.minLength();
    this.requireUppercase = constraintAnnotation.requireUppercase();
    this.requireLowercase = constraintAnnotation.requireLowercase();
    this.requireDigit = constraintAnnotation.requireDigit();
    this.requireSpecialChar = constraintAnnotation.requireSpecialChar();
  }

  @Override
  public boolean isValid(String password, ConstraintValidatorContext context) {
    if (password == null || password.isBlank()) {
      return false;
    }

    if (password.length() < minLength) {
      return false;
    }

    boolean hasUppercase = !requireUppercase || password.matches(".*[A-Z].*");
    boolean hasLowercase = !requireLowercase || password.matches(".*[a-z].*");
    boolean hasDigit = !requireDigit || password.matches(".*\\d.*");
    boolean hasSpecial = !requireSpecialChar || password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");

    return hasUppercase && hasLowercase && hasDigit && hasSpecial;
  }
}
