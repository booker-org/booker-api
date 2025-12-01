package com.booker.DTO.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePasswordDTO(
  @Size(min = 8, max = 64) @NotBlank
  String currentPassword,

  @Size(min = 8, max = 64) @NotBlank
  String newPassword
) {}