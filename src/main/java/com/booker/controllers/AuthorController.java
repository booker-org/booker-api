package com.booker.controllers;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.booker.DTO.Author.AuthorCreateDTO;
import com.booker.DTO.Author.AuthorDTO;
import com.booker.mappers.AuthorMapper;
import com.booker.models.Author;
import com.booker.services.AuthorService;

@RestController @RequestMapping("/authors")
@RequiredArgsConstructor
@Tag(name = "Authors", description = "Author management endpoints")
public class AuthorController {
  private final AuthorService authorService;
  private final AuthorMapper authorMapper;

  @GetMapping
  @Operation(summary = "Get all authors", description = "Get paginated list of all authors (max 100 per page)")
  public ResponseEntity<Page<AuthorDTO>> getAllAuthors(
    @ParameterObject @PageableDefault(size = 10, sort = "name") Pageable pageable
  ) {
    Page<Author> authors = authorService.findAll(pageable);
    Page<AuthorDTO> authorDTOs = authorMapper.toDTOPage(authors);

    return ResponseEntity.ok(authorDTOs);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get author by ID", description = "Get a specific author by its ID")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Author found"),
    @ApiResponse(responseCode = "404", description = "Author not found")
  })
  public ResponseEntity<AuthorDTO> getAuthorById(@Parameter(description = "Author ID") @PathVariable UUID id) {
    Optional<Author> author = authorService.findById(id);

    return author.map(authorMapper::toDTO)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  @Operation(summary = "Create new author", description = "Create a new author")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Author created successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid author data")
  })
  public ResponseEntity<AuthorDTO> createAuthor(@Valid @RequestBody AuthorCreateDTO authorCreateDTO) {
    Author author = authorMapper.toEntity(authorCreateDTO);
    Author savedAuthor = authorService.save(author);

    return ResponseEntity.status(HttpStatus.CREATED).body(authorMapper.toDTO(savedAuthor));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update author", description = "Update an existing author")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Author updated successfully"),
    @ApiResponse(responseCode = "404", description = "Author not found", content = @Content),
    @ApiResponse(responseCode = "400", description = "Invalid author data", content = @Content)
  })
  public ResponseEntity<AuthorDTO> updateAuthor(
    @Parameter(description = "Author ID") @PathVariable UUID id,
    @Valid @RequestBody AuthorCreateDTO authorCreateDTO
  ) {
    Author author = authorMapper.toEntity(authorCreateDTO);
    Optional<Author> updatedAuthor = authorService.update(id, author);

    return updatedAuthor.map(authorMapper::toDTO)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }

  @PatchMapping("/{id}")
  @Operation(summary = "Partially update author", description = "Partially update an existing author")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Author updated successfully"),
    @ApiResponse(responseCode = "404", description = "Author not found", content = @Content),
    @ApiResponse(responseCode = "400", description = "Invalid author data", content = @Content)
  })
  public ResponseEntity<AuthorDTO> patchAuthor(
    @Parameter(description = "Author ID") @PathVariable UUID id,
    @Valid @RequestBody AuthorCreateDTO authorCreateDTO
  ) {
    Author author = authorMapper.toEntity(authorCreateDTO);
    Optional<Author> updatedAuthor = authorService.partialUpdate(id, author);

    return updatedAuthor.map(authorMapper::toDTO)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete author", description = "Delete an author by ID")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "Author deleted successfully"),
    @ApiResponse(responseCode = "404", description = "Author not found")
  })
  public ResponseEntity<Void> deleteAuthor(@Parameter(description = "Author ID") @PathVariable UUID id) {
    boolean deleted = authorService.deleteById(id);

    return deleted
      ? ResponseEntity.noContent().build()
      : ResponseEntity.notFound().build();
  }
}