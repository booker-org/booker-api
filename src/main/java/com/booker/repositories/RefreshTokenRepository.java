package com.booker.repositories;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.booker.models.RefreshToken;
import com.booker.models.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
  Optional<RefreshToken> findByTokenHash(String tokenHash);

  List<RefreshToken> findByUserAndRevokedFalse(User user);

  @Modifying
  @Query("UPDATE RefreshToken rt SET rt.revoked = true, rt.revokedAt = :revokedAt WHERE rt.user = :user AND rt.revoked = false")
  void revokeAllUserTokens(User user, Instant revokedAt);

  @Modifying
  @Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < :now")
  void deleteExpiredTokens(Instant now);
}