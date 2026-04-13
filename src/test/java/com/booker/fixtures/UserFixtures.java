package com.booker.fixtures;

import java.time.LocalDateTime;

import com.booker.models.User;

import static com.booker.constants.Tests.DEFAULT_TEST_ID;

public final class UserFixtures {
  private UserFixtures() {}

  public static User validUser() {
    User user = new User();

    user.setId(DEFAULT_TEST_ID);
    user.setName("Name");
    user.setUsername("username");
    user.setEmail("email@email.com");
    user.setPassword("password");
    user.setBio("bio");
    user.setCreatedAt(LocalDateTime.now());
    user.setUpdatedAt(LocalDateTime.now());

    return user;
  }
}