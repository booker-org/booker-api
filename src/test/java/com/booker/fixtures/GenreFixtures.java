package com.booker.fixtures;

import com.booker.DTO.Genre.GenreDTO;

import static com.booker.constants.Tests.DEFAULT_TEST_ID;

public final class GenreFixtures {
  private GenreFixtures() {}

  public static GenreDTO validGenreDTO() {
    return new GenreDTO(
      DEFAULT_TEST_ID,
      "Name"
    );
  }
}