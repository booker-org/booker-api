package com.booker.DTO.User;

import java.time.LocalDateTime;
import java.util.UUID;

import com.booker.models.enums.Role;

public record AdminUserDTO(
  UUID id,
  String name,
  String username,
  String email,
  String bio,
  Role role,
  boolean accountNonLocked,
  LocalDateTime createdAt,
  LocalDateTime updatedAt
) {}
