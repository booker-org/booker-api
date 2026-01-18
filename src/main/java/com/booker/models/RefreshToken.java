package com.booker.models;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @Table(name = "refresh_tokens")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RefreshToken {
  @Id @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "token_hash", nullable = false, unique = true)
  private String tokenHash;

  @ManyToOne @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  @Column(nullable = false) @Builder.Default
  private Boolean revoked = false;

  @Column(name = "revoked_at")
  private Instant revokedAt;

  @Column(name = "device_info", length = 500)
  private String deviceInfo;

  @Column(name = "ip_address", length = 45)
  private String ipAddress;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @PrePersist
  protected void onCreate() {
    createdAt = Instant.now();
  }

  public boolean isValid() {
    return !revoked && expiresAt.isAfter(Instant.now());
  }
}
