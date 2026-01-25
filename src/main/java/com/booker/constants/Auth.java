package com.booker.constants;

public final class Auth {
  public static final String USER_ROLE = "USER";
  public static final String ADMIN_ROLE = "ADMIN";

  public static final String ADMIN_AUTHORIZATION = "hasRole('" + ADMIN_ROLE + "')";
  public static final String REVIEW_OWNER_OR_ADMIN = "@reviewService.isOwner(#id, authentication.principal.username) or " + ADMIN_AUTHORIZATION;

  private Auth() {}
}