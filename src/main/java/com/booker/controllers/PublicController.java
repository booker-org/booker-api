package com.booker.controllers;

import java.util.UUID;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.booker.DTO.Book.BookDTO;
import com.booker.DTO.Book.BookDetailDTO;
import com.booker.DTO.Genre.GenreDTO;
import com.booker.DTO.Review.SimpleReviewDTO;
import com.booker.mappers.GenreMapper;
import com.booker.mappers.ReviewMapper;
import com.booker.models.Genre;
import com.booker.models.Review;
import com.booker.services.BookService;
import com.booker.services.GenreService;
import com.booker.services.ReviewService;

import static com.booker.constants.Domain.PUBLIC_ENDPOINT;

@RestController
@RequestMapping(PUBLIC_ENDPOINT)
@RequiredArgsConstructor
@Tag(name = "Public", description = "Public endpoints (no authentication required)")
@SecurityRequirements
public class PublicController {
  private final BookService bookService;
  private final GenreService genreService;
  private final ReviewService reviewService;
  private final GenreMapper genreMapper;
  private final ReviewMapper reviewMapper;

  @GetMapping("/books")
  @Operation(summary = "Get all books", description = "Get paginated list of all books (max 100 per page)")
  public ResponseEntity<Page<BookDTO>> getAllBooks(
    @ParameterObject @PageableDefault(size = 10, sort = "title") Pageable pageable,
    @Parameter(description = "Filter by title") @RequestParam(required = false) String title,
    @Parameter(description = "Filter by author ID") @RequestParam(required = false) UUID authorId,
    @Parameter(description = "Search in title and synopsis") @RequestParam(required = false) String search
  ) {
    Page<BookDTO> books;

    if (title != null && !title.trim().isEmpty()) books = bookService.findByTitle(title, pageable);
    else if (authorId != null) books = bookService.findByAuthor(authorId, pageable);
    else if (search != null && !search.trim().isEmpty()) books = bookService.searchBooks(search, pageable);
    else books = bookService.findAll(pageable);

    return ResponseEntity.ok(books);
  }

  @GetMapping("/books/{id}")
  @Operation(summary = "Get book by ID", description = "Get a specific book by its ID")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Book found"),
    @ApiResponse(responseCode = "404", description = "Book not found")
  })
  public ResponseEntity<BookDetailDTO> getBookById(@Parameter(description = "Book ID") @PathVariable UUID id) {
    BookDetailDTO book = bookService.findById(id);

    return ResponseEntity.ok(book);
  }

  @GetMapping("/books/{id}/reviews")
  @Operation(summary = "Get reviews for a book", description = "Get paginated list of reviews for a specific book")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Reviews found"),
    @ApiResponse(responseCode = "404", description = "Book not found")
  })
  public ResponseEntity<Page<SimpleReviewDTO>> getReviewsForBook(
    @Parameter(description = "Book ID") @PathVariable UUID id,
    @ParameterObject Pageable pageable
  ) {
    Page<Review> reviews = reviewService.findByBookID(id, pageable);
    Page<SimpleReviewDTO> result = reviews.map(reviewMapper::toSimpleDTO);

    return ResponseEntity.ok(result);
  }

  @GetMapping("/genres")
  @Operation(summary = "Get all genres", description = "Get paginated list of all genres (max 100 per page)")
  public ResponseEntity<Page<GenreDTO>> getAllGenres(
    @ParameterObject @PageableDefault(size = 10, sort = "name") Pageable pageable
  ) {
    Page<Genre> genres = genreService.findAll(pageable);
    Page<GenreDTO> genreDTOs = genreMapper.toDTOPage(genres);

    return ResponseEntity.ok(genreDTOs);
  }
}