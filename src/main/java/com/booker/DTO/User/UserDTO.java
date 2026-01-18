package com.booker.DTO.User;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserDTO(
  UUID id,

  @Size(max = 100)
  @NotBlank
  String name,

  @Size(max = 30)
  @NotBlank
  String username,

  @Email
  @Size(max = 254)
  @NotBlank
  String email,

  @Size(max = 300)
  String bio,

  LocalDateTime createdAt,
  LocalDateTime updatedAt
) {}