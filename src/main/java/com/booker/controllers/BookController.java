package com.booker.controllers;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.booker.DTO.Book.BookCreateDTO;
import com.booker.DTO.Book.BookDTO;
import com.booker.DTO.Book.BookDetailDTO;
import com.booker.DTO.Review.SimpleReviewDTO;
import com.booker.mappers.BookMapper;
import com.booker.mappers.ReviewMapper;
import com.booker.models.Review;
import com.booker.services.BookService;
import com.booker.services.ReviewService;

import static com.booker.constants.Auth.ADMIN_ROLE;
import static com.booker.constants.Auth.ADMIN_AUTHORIZATION;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Tag(name = "Books", description = "Book management endpoints")
public class BookController {
  private final BookService bookService;
  private final ReviewService reviewService;
  private final BookMapper bookMapper;
  private final ReviewMapper reviewMapper;

  @GetMapping
  @Operation(summary = "Get all books", description = "Get paginated list of all books (max 100 per page)")
  public ResponseEntity<Page<BookDTO>> getAllBooks(
      @ParameterObject @PageableDefault(size = 10, sort = "title") Pageable pageable,
      @Parameter(description = "Filter by title") @RequestParam(required = false) String title,
      @Parameter(description = "Filter by author ID") @RequestParam(required = false) UUID authorId,
      @Parameter(description = "Search in title and synopsis") @RequestParam(required = false) String search) {
    Page<BookDTO> books;

    if (title != null && !title.trim().isEmpty())
      books = bookService.findByTitle(title, pageable);
    else if (authorId != null)
      books = bookService.findByAuthor(authorId, pageable);
    else if (search != null && !search.trim().isEmpty())
      books = bookService.searchBooks(search, pageable);
    else
      books = bookService.findAll(pageable);

    return ResponseEntity.ok(books);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get book by ID", description = "Get a specific book by its ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Book found"),
      @ApiResponse(responseCode = "404", description = "Book not found"),
  })
  public ResponseEntity<BookDetailDTO> getBookById(@Parameter(description = "Book ID") @PathVariable UUID id) {
    BookDetailDTO book = bookService.findById(id);

    return ResponseEntity.ok(book);
  }

  @PostMapping @PreAuthorize(ADMIN_AUTHORIZATION)
  @Operation(summary = "Create new book - " + ADMIN_ROLE, description = "Create a new book")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Book created successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid book data"),
  })
  public ResponseEntity<BookDetailDTO> createBook(@Valid @RequestBody BookCreateDTO book) {
    BookDetailDTO savedBook = bookService.save(bookMapper.toEntity(book), book.authorId(), book.genreIds());

    return ResponseEntity.created(URI.create("/books/" + savedBook.id())).body(savedBook);
  }

  @PutMapping(value = "/{id}") @PreAuthorize(ADMIN_AUTHORIZATION)
  @Operation(summary = "Update book - " + ADMIN_ROLE, description = "Update an existing book")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Book updated successfully"),
      @ApiResponse(responseCode = "404", description = "Book not found", content = @Content),
      @ApiResponse(responseCode = "400", description = "Invalid book data", content = @Content),
  })
  public ResponseEntity<BookDetailDTO> updateBook(
      @Parameter(description = "Book ID") @PathVariable UUID id,
      @Valid @RequestBody BookCreateDTO bookDTO) {
    Optional<BookDetailDTO> updatedBook = bookService.update(
        id,
        bookMapper.toEntity(bookDTO),
        bookDTO.authorId(),
        bookDTO.genreIds());

    return updatedBook.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }

  @PatchMapping(value = "/{id}") @PreAuthorize(ADMIN_AUTHORIZATION)
  @Operation(summary = "Partially update book - " + ADMIN_ROLE, description = "Partially update an existing book")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Book updated successfully"),
      @ApiResponse(responseCode = "404", description = "Book not found", content = @Content),
      @ApiResponse(responseCode = "400", description = "Invalid book data", content = @Content)
  })
  public ResponseEntity<BookDetailDTO> patchBook(
      @Parameter(description = "Book ID") @PathVariable UUID id,
      @RequestBody(required = false) BookCreateDTO book) {
    BookCreateDTO bookData = book != null
        ? book
        : new BookCreateDTO(
            null,
            null,
            null,
            null,
            null);

    Optional<BookDetailDTO> updatedBook = bookService.partialUpdate(
        id, bookMapper.toEntity(bookData),
        book != null ? book.authorId() : null, book != null ? book.genreIds() : null);

    return updatedBook
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping(value = "/{id}/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize(ADMIN_AUTHORIZATION)
  @Operation(summary = "Upload or replace book cover - " + ADMIN_ROLE, description = "Upload a new cover image for the book")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Cover uploaded successfully"),
      @ApiResponse(responseCode = "404", description = "Book not found", content = @Content),
      @ApiResponse(responseCode = "400", description = "Invalid file", content = @Content)
  })
  public ResponseEntity<BookDetailDTO> uploadCover(
      @Parameter(description = "Book ID") @PathVariable UUID id,
      @Parameter(description = "Cover image file", required = true) @RequestPart("cover") MultipartFile coverFile) {
    Optional<BookDetailDTO> updatedBook = bookService.updateCover(id, coverFile);

    return updatedBook
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}/cover") @PreAuthorize(ADMIN_AUTHORIZATION)
  @Operation(summary = "Remove book cover - " + ADMIN_ROLE, description = "Delete the existing cover image for the book")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Cover removed successfully"),
      @ApiResponse(responseCode = "404", description = "Book not found", content = @Content),
  })
  public ResponseEntity<Void> deleteCover(@Parameter(description = "Book ID") @PathVariable UUID id) {
    boolean removed = bookService.removeCover(id);

    return removed
        ? ResponseEntity.noContent().build()
        : ResponseEntity.notFound().build();
  }

  @DeleteMapping("/{id}") @PreAuthorize(ADMIN_AUTHORIZATION)
  @Operation(summary = "Delete book - " + ADMIN_ROLE, description = "Delete a book by ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Book deleted successfully"),
      @ApiResponse(responseCode = "404", description = "Book not found"),
  })
  public ResponseEntity<Void> deleteBook(@Parameter(description = "Book ID") @PathVariable UUID id) {
    boolean deleted = bookService.deleteById(id);

    return deleted
        ? ResponseEntity.noContent().build()
        : ResponseEntity.notFound().build();
  }

  @GetMapping("/{id}/reviews")
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
}