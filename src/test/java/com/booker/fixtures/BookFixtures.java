package com.booker.fixtures;

import java.time.LocalDateTime;
import java.util.List;

import com.booker.DTO.Book.BookDetailDTO;
import com.booker.models.Book;

import static com.booker.constants.Tests.DEFAULT_TEST_ID;

public final class BookFixtures {
  private static final String DEFAULT_TITLE = "Title";
  private static final String DEFAULT_SYNOPSIS = "Synopsis";
  private static final Integer DEFAULT_PAGE_COUNT = 1;
  private static final String DEFAULT_COVER_URL = "https://placehold.co/400x600";

  private BookFixtures() {}

  public static BookDetailDTO validBookDetailDTO() {
    return new BookDetailDTO(
      DEFAULT_TEST_ID,
      DEFAULT_TITLE,
      DEFAULT_SYNOPSIS,
      DEFAULT_PAGE_COUNT,
      AuthorFixtures.validAuthorDTO(),
      List.of(GenreFixtures.validGenreDTO()),
      DEFAULT_COVER_URL,
      LocalDateTime.now(),
      LocalDateTime.now()
    );
  }

  public static Book validBook() {
    Book book = new Book();

    book.setId(DEFAULT_TEST_ID);
    book.setTitle(DEFAULT_TITLE);
    book.setSynopsis(DEFAULT_SYNOPSIS);
    book.setPageCount(DEFAULT_PAGE_COUNT);
    book.setAuthor(AuthorFixtures.validAuthor());
    book.setCoverUrl(DEFAULT_COVER_URL);
    book.setCreatedAt(LocalDateTime.now());
    book.setUpdatedAt(LocalDateTime.now());

    return book;
  }
}