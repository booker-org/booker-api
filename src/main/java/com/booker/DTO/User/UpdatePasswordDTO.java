package com.booker.DTO.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.booker.validators.StrongPassword;

public record UpdatePasswordDTO(
  @NotBlank(message = "Current password is required")
  @Size(min = 8, max = 100, message = "Current password must be between 8 and 100 characters")
  String currentPassword,

  @NotBlank(message = "New password is required")
  @Size(min = 8, max = 100, message = "New password must be between 8 and 100 characters")
  @StrongPassword(message = "New password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
  String newPassword
) {}