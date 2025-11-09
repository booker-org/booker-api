package com.booker.controllers;

import java.util.Optional;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.booker.DTO.Author.BookCreateDTO;
import com.booker.DTO.Book.BookDTO;
import com.booker.DTO.Book.BookDetailDTO;
import com.booker.DTO.Book.BookPageResponse;
import com.booker.mappers.BookMapper;
import com.booker.models.Book;
import com.booker.services.BookService;

@RestController @RequestMapping("/books")
@Tag(name = "Books", description = "Book management endpoints")
public class BookController {
  @Autowired
  private BookService bookService;

  @Autowired
  private BookMapper bookMapper;

  @GetMapping
  @Operation(summary = "Get all books", description = "Get paginated list of all books")
  public ResponseEntity<BookPageResponse> getAllBooks(
    @ParameterObject @PageableDefault(size = 10, sort = "title") Pageable pageable,
    @Parameter(description = "Filter by title") @RequestParam(required = false) String title,
    @Parameter(description = "Filter by author ID") @RequestParam(required = false) UUID authorId,
    @Parameter(description = "Search in title and synopsis") @RequestParam(required = false) String search
  ) {
    Page<Book> books;

    if (title != null && !title.trim().isEmpty()) books = bookService.findByTitle(title, pageable);
    else if (authorId != null) books = bookService.findByAuthor(authorId, pageable);
    else books = bookService.findAll(pageable);

    BookPageResponse response = bookMapper.toPageResponse(books);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get book by ID", description = "Get a specific book by its ID")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Book found"),
    @ApiResponse(responseCode = "404", description = "Book not found")
  })
  public ResponseEntity<BookDetailDTO> getBookById(@Parameter(description = "Book ID") @PathVariable UUID id) {
    Optional<Book> book = bookService.findById(id);

    return book.map(bookMapper::toDetailDTO)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build())
    ;
  }

  @PostMapping
  @Operation(summary = "Create new book", description = "Create a new book")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Book created successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid book data")
  })
  public ResponseEntity<BookDTO> createBook(@RequestBody BookCreateDTO book) {
    Book savedBook = bookService.save(bookMapper.toEntity(book), book.authorId(), book.genreIds());

    return ResponseEntity.status(HttpStatus.CREATED).body(bookMapper.toDTO(savedBook));
  }

  @PutMapping(value = "/{id}")
  @Operation(summary = "Update book", description = "Update an existing book")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Book updated successfully"),
    @ApiResponse(responseCode = "404", description = "Book not found", content = @Content),
    @ApiResponse(responseCode = "400", description = "Invalid book data", content = @Content)
  })
  public ResponseEntity<BookDTO> updateBook(
    @Parameter(description = "Book ID") @PathVariable UUID id,
    @RequestBody BookCreateDTO bookDTO
  ) {
    Optional<Book> updatedBook = bookService.update(
      id,
      bookMapper.toEntity(bookDTO),
      bookDTO.authorId(),
      bookDTO.genreIds()
    );

    return updatedBook.map(bookMapper::toDTO).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }

  @PatchMapping(value = "/{id}")
  @Operation(summary = "Partially update book", description = "Partially update an existing book")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Book updated successfully"),
    @ApiResponse(responseCode = "404", description = "Book not found", content = @Content),
    @ApiResponse(responseCode = "400", description = "Invalid book data", content = @Content)
  })
  public ResponseEntity<BookDTO> patchBook(
    @Parameter(description = "Book ID") @PathVariable UUID id,
    @RequestBody(required = false) BookCreateDTO book
  ) {
    BookCreateDTO bookData = book != null ? book : new BookCreateDTO(
      null,
      null,
      null,
      null,
      null
    );

    Optional<Book> updatedBook = bookService.partialUpdate(
      id, bookMapper.toEntity(bookData),
      book != null ? book.authorId() : null, book != null ? book.genreIds() : null
    );

    return updatedBook.map(bookMapper::toDTO)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build())
    ;
  }

  @PutMapping(value = "/{id}/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "Upload or replace book cover", description = "Upload a new cover image for the book")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Cover uploaded successfully"),
    @ApiResponse(responseCode = "404", description = "Book not found", content = @Content),
    @ApiResponse(responseCode = "400", description = "Invalid file", content = @Content)
  })
  public ResponseEntity<BookDTO> uploadCover(
    @Parameter(description = "Book ID") @PathVariable UUID id,
    @Parameter(description = "Cover image file", required = true) @RequestPart("cover") MultipartFile coverFile
  ) {
    Optional<Book> updatedBook = bookService.updateCover(id, coverFile);

    return updatedBook.map(bookMapper::toDTO)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build())
    ;
  }

  @DeleteMapping("/{id}/cover")
  @Operation(summary = "Remove book cover", description = "Delete the existing cover image for the book")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "Cover removed successfully"),
    @ApiResponse(responseCode = "404", description = "Book not found", content = @Content)
  })
  public ResponseEntity<Void> deleteCover(@Parameter(description = "Book ID") @PathVariable UUID id) {
    Optional<Book> book = bookService.removeCover(id);

    if (book.isEmpty()) return ResponseEntity.notFound().build();

    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete book", description = "Delete a book by ID")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "Book deleted successfully"),
    @ApiResponse(responseCode = "404", description = "Book not found")
  })
  public ResponseEntity<Void> deleteBook(@Parameter(description = "Book ID") @PathVariable UUID id) {
    boolean deleted = bookService.deleteById(id);

    return deleted
      ? ResponseEntity.noContent().build()
      : ResponseEntity.notFound().build()
    ;
  }
}