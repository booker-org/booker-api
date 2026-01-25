package com.booker.models.enums;

import lombok.Getter;

import static com.booker.constants.Auth.USER_ROLE;
import static com.booker.constants.Auth.ADMIN_ROLE;

public enum Role {
  USER(USER_ROLE),
  ADMIN(ADMIN_ROLE);

  @Getter
  private final String role;

  Role(String role) { this.role = role; }
}