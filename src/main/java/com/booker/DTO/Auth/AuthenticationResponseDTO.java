package com.booker.DTO.Auth;

import com.booker.DTO.User.UserDTO;

public record AuthenticationResponseDTO(
  String accessToken,
  String refreshToken,
  String tokenType,
  Long expiresIn,
  UserDTO user) {
  public AuthenticationResponseDTO(String accessToken, String refreshToken, String tokenType, Long expiresIn,
    UserDTO user) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.tokenType = tokenType != null ? tokenType : "Bearer";
    this.expiresIn = expiresIn;
    this.user = user;
  }
}
