package com.booker.DTO.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserDTO(
  @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
  String name,

  @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
  String username,

  @Email(message = "Email must be valid")
  @Size(max = 254, message = "Email must not exceed 254 characters")
  String email,

  @Size(max = 300, message = "Bio must not exceed 300 characters")
  String bio
) {}