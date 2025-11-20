package com.booker.DTO.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserDTO(
  @Size(max = 100) @NotBlank
  String name,

  @Size(max = 30) @NotBlank
  String username,

  @Email @Size(max = 254) @NotBlank
  String email,

  @Size(max = 255) @NotBlank
  String password,

  @Size(max = 300)
  String bio
) {}