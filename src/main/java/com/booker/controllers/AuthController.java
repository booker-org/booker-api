package com.booker.controllers;

import java.net.URI;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.booker.DTO.Auth.AuthenticationResponseDTO;
import com.booker.DTO.Auth.LoginRequestDTO;
import com.booker.DTO.Auth.RefreshTokenRequestDTO;
import com.booker.DTO.Auth.RegisterRequestDTO;
import com.booker.services.AuthenticationService;

@RestController @RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {
  private final AuthenticationService authenticationService;

  @PostMapping("/register")
  @Operation(summary = "Register a new user", description = "Creates a new user account and returns authentication tokens")
  @SecurityRequirements
  public ResponseEntity<AuthenticationResponseDTO> register(
    @Valid @RequestBody RegisterRequestDTO request,
    HttpServletRequest httpRequest
  ) {
    AuthenticationResponseDTO response = authenticationService.register(request, httpRequest);
    URI location = URI.create("/users/" + response.user().id());

    return ResponseEntity.created(location).body(response);
  }

  @PostMapping("/login")
  @Operation(summary = "Login user", description = "Authenticates user and returns access and refresh tokens")
  @SecurityRequirements
  public ResponseEntity<AuthenticationResponseDTO> login(
    @Valid @RequestBody LoginRequestDTO request,
    HttpServletRequest httpRequest
  ) {
    AuthenticationResponseDTO response = authenticationService.login(request, httpRequest);

    return ResponseEntity.ok(response);
  }

  @PostMapping("/refresh")
  @Operation(
    summary = "Refresh access token",
    description = "Generates new access and refresh tokens using a valid refresh token"
  )
  @SecurityRequirements
  public ResponseEntity<AuthenticationResponseDTO> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO request) {
    AuthenticationResponseDTO response = authenticationService.refreshToken(request);

    return ResponseEntity.ok(response);
  }

  @PostMapping("/logout")
  @Operation(summary = "Logout user", description = "Revokes the provided refresh token")
  @SecurityRequirements
  public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequestDTO request) {
    authenticationService.logout(request.refreshToken());

    return ResponseEntity.noContent().build();
  }
}