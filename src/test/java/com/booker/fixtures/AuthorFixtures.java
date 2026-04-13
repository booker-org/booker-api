package com.booker.fixtures;

import java.time.LocalDateTime;

import com.booker.DTO.Author.AuthorDTO;
import com.booker.models.Author;

import static com.booker.constants.Tests.DEFAULT_TEST_ID;

public final class AuthorFixtures {
  private static final String DEFAULT_NAME = "Name";
  private static final String DEFAULT_BIOGRAPHY = "Name";

  private AuthorFixtures() {}

  public static AuthorDTO validAuthorDTO() {
    return new AuthorDTO(
      DEFAULT_TEST_ID,
      DEFAULT_NAME,
      DEFAULT_BIOGRAPHY
    );
  }

  public static Author validAuthor() {
    Author author = new Author();

    author.setId(DEFAULT_TEST_ID);
    author.setName(DEFAULT_NAME);
    author.setBiography(DEFAULT_BIOGRAPHY);
    author.setCreatedAt(LocalDateTime.now());
    author.setUpdatedAt(LocalDateTime.now());

    return author;
  }
}