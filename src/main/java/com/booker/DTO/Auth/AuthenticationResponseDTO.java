package com.booker.DTO.Auth;

import com.booker.DTO.User.UserDTO;

public record AuthenticationResponseDTO(
  String accessToken,
  String refreshToken,
  String tokenType,
  Long expiresIn,
  UserDTO user
) {}