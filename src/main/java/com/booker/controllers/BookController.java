package com.booker.controllers;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.booker.DTO.Book.BookCreateDTO;
import jakarta.validation.Valid;
import com.booker.DTO.Book.BookDTO;
import com.booker.DTO.Book.BookDetailDTO;
import com.booker.mappers.BookMapper;
import com.booker.services.BookService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Tag(name = "Books", description = "Book management endpoints")
public class BookController {
  private final BookService bookService;
  private final BookMapper bookMapper;

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
    @ApiResponse(responseCode = "200", description = "Livro encontrado"),
    @ApiResponse(responseCode = "404", description = "Livro não encontrado")
  })
  public ResponseEntity<BookDetailDTO> getBookById(@Parameter(description = "Book ID") @PathVariable UUID id) {
    BookDetailDTO book = bookService.findById(id);
    return ResponseEntity.ok(book);
  }

  @PostMapping
  @Operation(summary = "Create new book", description = "Create a new book")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Livro criado com sucesso"),
    @ApiResponse(responseCode = "400", description = "Dados de livro inválidos")
  })
  public ResponseEntity<BookDetailDTO> createBook(@Valid @RequestBody BookCreateDTO book) {
    BookDetailDTO savedBook = bookService.save(bookMapper.toEntity(book), book.authorId(), book.genreIds());

    return ResponseEntity.created(URI.create("/books/" + savedBook.id())).body(savedBook);
  }

  @PutMapping(value = "/{id}")
  @Operation(summary = "Update book", description = "Update an existing book")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Livro atualizado com sucesso"),
    @ApiResponse(responseCode = "404", description = "Livro não encontrado", content = @Content),
    @ApiResponse(responseCode = "400", description = "Dados de livro inválidos", content = @Content)
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

  @PatchMapping(value = "/{id}")
  @Operation(summary = "Partially update book", description = "Partially update an existing book")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Book updated successfully"),
    @ApiResponse(responseCode = "404", description = "Book not found", content = @Content),
    @ApiResponse(responseCode = "400", description = "Invalid book data", content = @Content)
  })
  public ResponseEntity<BookDetailDTO> patchBook(
    @Parameter(description = "Book ID") @PathVariable UUID id,
    @RequestBody(required = false) BookCreateDTO book) {
    BookCreateDTO bookData = book != null ? book
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
  @Operation(summary = "Upload or replace book cover", description = "Upload a new cover image for the book")
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

  @DeleteMapping("/{id}/cover")
  @Operation(summary = "Remove book cover", description = "Delete the existing cover image for the book")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "Capa removida com sucesso"),
    @ApiResponse(responseCode = "404", description = "Livro não encontrado", content = @Content)
  })
  public ResponseEntity<Void> deleteCover(@Parameter(description = "Book ID") @PathVariable UUID id) {
    boolean removed = bookService.removeCover(id);

    return removed
      ? ResponseEntity.noContent().build()
      : ResponseEntity.notFound().build();
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete book", description = "Delete a book by ID")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "Livro excluído com sucesso"),
    @ApiResponse(responseCode = "404", description = "Livro não encontrado")
  })
  public ResponseEntity<Void> deleteBook(@Parameter(description = "Book ID") @PathVariable UUID id) {
    boolean deleted = bookService.deleteById(id);

    return deleted
      ? ResponseEntity.noContent().build()
      : ResponseEntity.notFound().build();
  }
}