package com.booker.DTO.Auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
  @NotBlank(message = "Username or email is required") String usernameOrEmail,

  @NotBlank(message = "Password is required") String password) {
}
