package com.booker.DTO.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePasswordDTO(
  @Size(max = 255) @NotBlank
  String currentPassword,

  @Size(max = 255) @NotBlank
  String newPassword
) {}