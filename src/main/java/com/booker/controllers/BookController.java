package com.booker.controllers;

import com.booker.models.Book;
import com.booker.services.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/books")
@Tag(name = "Books", description = "Book management endpoints")
public class BookController {

    @Autowired
    private BookService bookService;

    // GET /books - Lista todos os livros com paginação
    @GetMapping
    @Operation(summary = "Get all books", description = "Get paginated list of all books")
    public ResponseEntity<Page<Book>> getAllBooks(
        @PageableDefault(size = 10, sort = "title") Pageable pageable,
        @Parameter(description = "Filter by title") @RequestParam(required = false) String title,
        @Parameter(description = "Filter by author ID") @RequestParam(required = false) Long authorId
    ) {
        Page<Book> books;
        
        if (title != null && !title.trim().isEmpty()) {
            books = bookService.findByTitle(title, pageable);
        } else if (authorId != null) {
            books = bookService.findByAuthor(authorId, pageable);
        } else {
            books = bookService.findAll(pageable);
        }
        
        return ResponseEntity.ok(books);
    }

    // GET /books/{id} - Buscar livro por ID
    @GetMapping("/{id}")
    @Operation(summary = "Get book by ID", description = "Get a specific book by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Book found"),
        @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<Book> getBookById(
        @Parameter(description = "Book ID") @PathVariable Long id
    ) {
        Optional<Book> book = bookService.findById(id);
        return book.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    // POST /books - Criar novo livro
    @PostMapping
    @Operation(summary = "Create new book", description = "Create a new book")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Book created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid book data")
    })
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        Book savedBook = bookService.save(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
    }

    // PUT /books/{id} - Atualizar livro
    @PutMapping("/{id}")
    @Operation(summary = "Update book", description = "Update an existing book")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Book updated successfully"),
        @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<Book> updateBook(
        @Parameter(description = "Book ID") @PathVariable Long id,
        @RequestBody Book book
    ) {
        Optional<Book> updatedBook = bookService.update(id, book);
        return updatedBook.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /books/{id} - Deletar livro
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete book", description = "Delete a book by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Book deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<Void> deleteBook(
        @Parameter(description = "Book ID") @PathVariable Long id
    ) {
        boolean deleted = bookService.deleteById(id);
        return deleted ? ResponseEntity.noContent().build() 
                      : ResponseEntity.notFound().build();
    }
}
