package com.booker.services;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import jakarta.annotation.PostConstruct;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.booker.config.security.SecurityConstants;
import com.booker.models.User;

@Service
public class JwtService {
  @Value("${jwt.secret}")
  private String secretKey;

  @Value("${jwt.access-token.expiration}")
  private long accessTokenExpiration; // em milisegundos

  @Value("${jwt.refresh-token.expiration}")
  private long refreshTokenExpiration; // em milisegundos

  @PostConstruct
  public void validateSecretKey() {
    if (secretKey == null || secretKey.isBlank()) {
      throw new IllegalStateException("A chave secreta JWT não pode ser nula ou vazia");
    }

    byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);

    if (keyBytes.length < 32) {
      throw new IllegalStateException(String.format(
        "A chave secreta JWT deve ter no mínimo 256 bits (32 bytes) para o algoritmo HS256. A chave atual tem apenas %d bytes.",
        keyBytes.length
      ));
    }
  }

  public String generateAccessToken(User user) {
    Map<String, Object> claims = new HashMap<>();

    claims.put(SecurityConstants.CLAIM_USER_ID, user.getId().toString());
    claims.put("email", user.getEmail());
    claims.put(SecurityConstants.CLAIM_ROLE, user.getRole().name());
    claims.put(SecurityConstants.CLAIM_TOKEN_TYPE, SecurityConstants.TOKEN_TYPE_ACCESS);

    return buildToken(claims, user.getUsername(), accessTokenExpiration);
  }

  public String generateRefreshToken(User user) {
    Map<String, Object> claims = new HashMap<>();

    claims.put(SecurityConstants.CLAIM_USER_ID, user.getId().toString());
    claims.put(SecurityConstants.CLAIM_TOKEN_TYPE, SecurityConstants.TOKEN_TYPE_REFRESH);

    return buildToken(claims, user.getUsername(), refreshTokenExpiration);
  }

  private String buildToken(Map<String, Object> claims, String subject, long expiration) {
    Instant now = Instant.now();
    Instant expiryDate = now.plusMillis(expiration);

    return Jwts.builder()
      .claims(claims)
      .subject(subject)
      .issuedAt(Date.from(now))
      .expiration(Date.from(expiryDate))
      .signWith(getSigningKey())
      .compact();
  }

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public String extractUserId(String token) {
    return extractClaim(token, claims -> claims.get("userId", String.class));
  }

  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);

    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser()
      .verifyWith(getSigningKey())
      .build()
      .parseSignedClaims(token)
      .getPayload();
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);

    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }

  public boolean isTokenType(String token, String expectedType) {
    try {
      String tokenType = extractClaim(token, claims -> claims.get(SecurityConstants.CLAIM_TOKEN_TYPE, String.class));

      return expectedType.equals(tokenType);
    } catch (Exception e) { return false; }
  }

  public boolean isAccessToken(String token) {
    return isTokenType(token, SecurityConstants.TOKEN_TYPE_ACCESS);
  }

  public boolean isRefreshToken(String token) {
    return isTokenType(token, SecurityConstants.TOKEN_TYPE_REFRESH);
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private SecretKey getSigningKey() {
    byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);

    return Keys.hmacShaKeyFor(keyBytes);
  }

  public long getAccessTokenExpirationInSeconds() {
    return accessTokenExpiration / 1000;
  }

  public long getRefreshTokenExpirationInSeconds() {
    return refreshTokenExpiration / 1000;
  }
}