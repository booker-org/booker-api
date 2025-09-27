package com.booker.controllers;

import com.booker.dtos.BookCreateDTO;
import com.booker.dtos.BookDTO;
import com.booker.dtos.BookDetailDTO;
import com.booker.dtos.BookPageResponse;
import com.booker.entities.Book;
import com.booker.mappers.BookMapper;
import com.booker.services.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/books")
@Tag(name = "Books", description = "Book management endpoints")
public class BookController {

  @Autowired
  private BookService bookService;

  @Autowired
  private BookMapper bookMapper;

  // GET /books - Lista todos os livros com paginação
  @GetMapping
  @Operation(summary = "Get all books", description = "Get paginated list of all books")
  public ResponseEntity<BookPageResponse> getAllBooks(
      @ParameterObject @PageableDefault(size = 10, sort = "title") Pageable pageable,
      @Parameter(description = "Filter by title") @RequestParam(required = false) String title,
      @Parameter(description = "Filter by author ID") @RequestParam(required = false) Long authorId,
      @Parameter(description = "Search in title and synopsis") @RequestParam(required = false) String search) {
    Page<Book> books;

    if (title != null && !title.trim().isEmpty()) {
      books = bookService.findByTitle(title, pageable);
    } else if (authorId != null) {
      books = bookService.findByAuthor(authorId, pageable);
    } else {
      books = bookService.findAll(pageable);
    }

    BookPageResponse response = bookMapper.toPageResponse(books);

    return ResponseEntity.ok(response);
  }

  // GET /books/{id} - Buscar livro por ID
  @GetMapping("/{id}")
  @Operation(summary = "Get book by ID", description = "Get a specific book by its ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Book found"),
      @ApiResponse(responseCode = "404", description = "Book not found")
  })
  public ResponseEntity<BookDetailDTO> getBookById(
      @Parameter(description = "Book ID") @PathVariable Long id) {
    Optional<Book> book = bookService.findById(id);
    return book.map(bookMapper::toDetailDTO)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  // POST /books - Criar novo livro
  @PostMapping(consumes = "multipart/form-data")
  @Operation(summary = "Create new book", description = "Create a new book")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Book created successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid book data")
  })
  public ResponseEntity<BookDTO> createBook(
      @Parameter(description = "Book data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookCreateDTO.class))) @RequestPart("book") BookCreateDTO book,
      @Parameter(description = "Cover image file (optional)") @RequestPart(value = "cover", required = false) MultipartFile coverFile) {
    try {
      Book savedBook = bookService.save(bookMapper.toEntity(book), book.authorId(), book.genreIds(), coverFile);
      return ResponseEntity.status(HttpStatus.CREATED).body(bookMapper.toDTO(savedBook));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  // PUT /books/{id} - Atualizar livro
  @PutMapping(value = "/{id}", consumes = "multipart/form-data")
  @Operation(summary = "Update book", description = "Update an existing book")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Book updated successfully"),
      @ApiResponse(responseCode = "404", description = "Book not found", content = @Content),
      @ApiResponse(responseCode = "400", description = "Invalid book data", content = @Content)
  })
  public ResponseEntity<BookDTO> updateBook(
      @Parameter(description = "Book ID") @PathVariable Long id,
      @Parameter(description = "Book data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookCreateDTO.class))) @RequestPart("book") BookCreateDTO bookDTO,
      @Parameter(description = "Cover image file (optional)") @RequestPart(value = "cover", required = false) org.springframework.web.multipart.MultipartFile coverFile) {
    try {
      Optional<Book> updatedBook = bookService.update(id, bookMapper.toEntity(bookDTO), bookDTO.authorId(),
          bookDTO.genreIds(), coverFile);
      return updatedBook.map(bookMapper::toDTO).map(ResponseEntity::ok)
          .orElse(ResponseEntity.notFound().build());
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  // PATCH /books/{id} - Atualização parcial
  @PatchMapping(value = "/{id}", consumes = "multipart/form-data")
  @Operation(summary = "Partially update book", description = "Partially update an existing book")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Book updated successfully"),
      @ApiResponse(responseCode = "404", description = "Book not found", content = @Content),
      @ApiResponse(responseCode = "400", description = "Invalid book data", content = @Content)
  })
  public ResponseEntity<BookDTO> patchBook(
      @Parameter(description = "Book ID") @PathVariable Long id,
      @Parameter(description = "Book data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookCreateDTO.class))) @RequestPart(value = "book", required = false) BookCreateDTO book,
      @Parameter(description = "Cover image file (optional)") @RequestPart(value = "cover", required = false) MultipartFile coverFile) {
    try {
      BookCreateDTO bookData = book != null ? book : new BookCreateDTO(null, null, null, null, null);

      Optional<Book> updatedBook = bookService.partialUpdate(id, bookMapper.toEntity(bookData),
          book != null ? book.authorId() : null, book != null ? book.genreIds() : null, coverFile);
      return updatedBook.map(bookMapper::toDTO)
          .map(ResponseEntity::ok)
          .orElse(ResponseEntity.notFound().build());
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  // DELETE /books/{id} - Deletar livro
  @DeleteMapping("/{id}")
  @Operation(summary = "Delete book", description = "Delete a book by ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Book deleted successfully"),
      @ApiResponse(responseCode = "404", description = "Book not found")
  })
  public ResponseEntity<Void> deleteBook(
      @Parameter(description = "Book ID") @PathVariable Long id) {
    boolean deleted = bookService.deleteById(id);
    return deleted ? ResponseEntity.noContent().build()
        : ResponseEntity.notFound().build();
  }
}
