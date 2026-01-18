package com.booker.DTO.User;

import com.booker.models.enums.Role;
import com.booker.validators.StrongPassword;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserDTO(
  @NotBlank(message = "Name is required") @Size(min = 2, max = 100,
    message = "Name must be between 2 and 100 characters") String name,

  @NotBlank(message = "Username is required") @Size(min = 3, max = 30,
    message = "Username must be between 3 and 30 characters") String username,

  @NotBlank(message = "Email is required") @Email(message = "Email must be valid") @Size(max = 254,
    message = "Email must not exceed 254 characters") String email,

  @NotBlank(message = "Password is required") @Size(min = 8, max = 100,
    message = "Password must be between 8 and 100 characters") @StrongPassword(
      message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character") String password,

  @Size(max = 300, message = "Bio must not exceed 300 characters") String bio,

  @NotNull(message = "Role is required") Role role,

  @NotNull(message = "Enabled status is required") Boolean enabled,

  @NotNull(message = "Account lock status is required") Boolean accountNonLocked) {
}