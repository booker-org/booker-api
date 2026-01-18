package com.booker.config.security;

public final class SecurityConstants {
  private SecurityConstants() {}

  // JWT Token Types
  public static final String TOKEN_TYPE_ACCESS = "access";
  public static final String TOKEN_TYPE_REFRESH = "refresh";

  // JWT Claims
  public static final String CLAIM_TOKEN_TYPE = "type";
  public static final String CLAIM_USER_ID = "userId";
  public static final String CLAIM_ROLE = "role";

  // HTTP Headers
  public static final String HEADER_AUTHORIZATION = "Authorization";
  public static final String HEADER_USER_AGENT = "User-Agent";
  public static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";

  // Authentication
  public static final String BEARER_PREFIX = "Bearer ";
  public static final int BEARER_PREFIX_LENGTH = 7;
  public static final String ROLE_PREFIX = "ROLE_";
}