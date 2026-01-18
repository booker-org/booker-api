package com.booker.services;

import com.booker.DTO.Auth.AuthenticationResponseDTO;
import com.booker.DTO.Auth.LoginRequestDTO;
import com.booker.DTO.Auth.RefreshTokenRequestDTO;
import com.booker.DTO.Auth.RegisterRequestDTO;
import com.booker.config.security.SecurityConstants;
import com.booker.exceptions.ResourceNotFoundException;
import com.booker.mappers.UserMapper;
import com.booker.models.RefreshToken;
import com.booker.models.User;
import com.booker.models.enums.Role;
import com.booker.repositories.RefreshTokenRepository;
import com.booker.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final UserRepository userRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final UserMapper userMapper;

  @Transactional
  public AuthenticationResponseDTO register(RegisterRequestDTO request, HttpServletRequest httpRequest) {
    if (userRepository.existsByUsername(request.username())) {
      throw new IllegalArgumentException("Nome de usuário já existe");
    }

    if (userRepository.existsByEmail(request.email())) {
      throw new IllegalArgumentException("Email já existe");
    }

    User user = new User();
    user.setName(request.name());
    user.setUsername(request.username());
    user.setEmail(request.email());
    user.setPassword(passwordEncoder.encode(request.password()));
    user.setRole(Role.USER);
    user.setEnabled(true);
    user.setAccountNonLocked(true);

    User savedUser = userRepository.save(user);

    String accessToken = jwtService.generateAccessToken(savedUser);
    String refreshToken = jwtService.generateRefreshToken(savedUser);

    String deviceInfo = extractDeviceInfo(httpRequest);
    String ipAddress = extractIpAddress(httpRequest);
    saveRefreshToken(savedUser, refreshToken, deviceInfo, ipAddress);

    return new AuthenticationResponseDTO(
      accessToken,
      refreshToken,
      SecurityConstants.BEARER_PREFIX.trim(),
      jwtService.getAccessTokenExpirationInSeconds(),
      userMapper.toDTO(savedUser)
    );
  }

  @Transactional
  public AuthenticationResponseDTO login(LoginRequestDTO request, HttpServletRequest httpRequest) {
    authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(
        request.usernameOrEmail(),
        request.password()));

    User user = userRepository.findByUsername(request.usernameOrEmail())
      .or(() -> userRepository.findByEmail(request.usernameOrEmail()))
      .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    String accessToken = jwtService.generateAccessToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);

    String deviceInfo = extractDeviceInfo(httpRequest);
    String ipAddress = extractIpAddress(httpRequest);
    saveRefreshToken(user, refreshToken, deviceInfo, ipAddress);

    return new AuthenticationResponseDTO(
      accessToken,
      refreshToken,
      SecurityConstants.BEARER_PREFIX.trim(),
      jwtService.getAccessTokenExpirationInSeconds(),
      userMapper.toDTO(user)
    );
  }

  @Transactional
  public AuthenticationResponseDTO refreshToken(RefreshTokenRequestDTO request) {
    String refreshTokenValue = request.refreshToken();

    if (!jwtService.isRefreshToken(refreshTokenValue)) {
      throw new IllegalArgumentException("Tipo de token inválido. Esperado refresh token.");
    }

    String username = jwtService.extractUsername(refreshTokenValue);
    if (username == null || username.isBlank()) {
      throw new IllegalArgumentException("Refresh token inválido");
    }

    String tokenHash = hashToken(refreshTokenValue);

    RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
      .orElseThrow(() -> new IllegalArgumentException("Refresh token inválido"));

    if (!refreshToken.isValid()) {
      throw new IllegalArgumentException("Refresh token expirado ou revogado");
    }

    User user = refreshToken.getUser();

    String newAccessToken = jwtService.generateAccessToken(user);
    String newRefreshToken = jwtService.generateRefreshToken(user);

    refreshToken.setRevoked(true);
    refreshToken.setRevokedAt(Instant.now());
    refreshTokenRepository.save(refreshToken);

    saveRefreshToken(user, newRefreshToken, refreshToken.getDeviceInfo(), refreshToken.getIpAddress());

    return new AuthenticationResponseDTO(
      newAccessToken,
      newRefreshToken,
      SecurityConstants.BEARER_PREFIX.trim(),
      jwtService.getAccessTokenExpirationInSeconds(),
      userMapper.toDTO(user)
    );
  }

  @Transactional
  public void logout(String refreshTokenValue) {
    if (refreshTokenValue == null || refreshTokenValue.isBlank()) {
      return;
    }

    String tokenHash = hashToken(refreshTokenValue);

    refreshTokenRepository.findByTokenHash(tokenHash).ifPresent(token -> {
      token.setRevoked(true);
      token.setRevokedAt(Instant.now());
      refreshTokenRepository.save(token);
    });
  }

  @Transactional
  public void logoutAllDevices(User user) {
    refreshTokenRepository.revokeAllUserTokens(user, Instant.now());
  }

  private void saveRefreshToken(User user, String token, String deviceInfo, String ipAddress) {
    String tokenHash = hashToken(token);
    long expirationSeconds = jwtService.getRefreshTokenExpirationInSeconds();
    Instant expiresAt = Instant.now().plusSeconds(expirationSeconds);

    RefreshToken refreshToken = RefreshToken.builder()
      .tokenHash(tokenHash)
      .user(user)
      .expiresAt(expiresAt)
      .revoked(false)
      .deviceInfo(deviceInfo)
      .ipAddress(ipAddress)
      .build();

    refreshTokenRepository.save(refreshToken);
  }

  private String hashToken(String token) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(hash);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Erro ao gerar hash do token", e);
    }
  }

  private String extractDeviceInfo(HttpServletRequest request) {
    String userAgent = request.getHeader(SecurityConstants.HEADER_USER_AGENT);
    return userAgent != null ? userAgent.substring(0, Math.min(userAgent.length(), 500)) : null;
  }

  private String extractIpAddress(HttpServletRequest request) {
    String xForwardedFor = request.getHeader(SecurityConstants.HEADER_X_FORWARDED_FOR);
    if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
      return xForwardedFor.split(",")[0].trim();
    }
    return request.getRemoteAddr();
  }
}
