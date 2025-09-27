package com.booker.controllers;

import com.booker.dtos.AuthorDTO;
import com.booker.dtos.AuthorCreateDTO;
import com.booker.entities.Author;
import com.booker.mappers.AuthorMapper;
import com.booker.services.AuthorService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/authors")
@Tag(name = "Authors", description = "Author management endpoints")
public class AuthorController {

  @Autowired
  private AuthorService authorService;

  @Autowired
  private AuthorMapper authorMapper;

  // GET /authors - Lista todos os autores com paginação
  @GetMapping
  @Operation(summary = "Get all authors", description = "Get paginated list of all authors")
  public ResponseEntity<List<AuthorDTO>> getAllAuthors(
      @ParameterObject @PageableDefault(size = 10, sort = "name") Pageable pageable,
      @Parameter(description = "Get paginated results") @RequestParam(required = false, defaultValue = "false") boolean paginated) {
    
    if (paginated) {
      Page<Author> authors = authorService.findAll(pageable);
      List<AuthorDTO> authorDTOs = authorMapper.toDTOList(authors.getContent());
      return ResponseEntity.ok(authorDTOs);
    } else {
      List<Author> authors = authorService.findAll();
      List<AuthorDTO> authorDTOs = authorMapper.toDTOList(authors);
      return ResponseEntity.ok(authorDTOs);
    }
  }

  // GET /authors/{id} - Buscar autor por ID
  @GetMapping("/{id}")
  @Operation(summary = "Get author by ID", description = "Get a specific author by its ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Author found"),
      @ApiResponse(responseCode = "404", description = "Author not found")
  })
  public ResponseEntity<AuthorDTO> getAuthorById(
      @Parameter(description = "Author ID") @PathVariable Long id) {
    Optional<Author> author = authorService.findById(id);
    return author.map(authorMapper::toDTO)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  // POST /authors - Criar novo autor
  @PostMapping
  @Operation(summary = "Create new author", description = "Create a new author")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Author created successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid author data")
  })
  public ResponseEntity<AuthorDTO> createAuthor(@RequestBody AuthorCreateDTO authorCreateDTO) {
    try {
      Author author = authorMapper.toEntity(authorCreateDTO);
      Author savedAuthor = authorService.save(author);
      return ResponseEntity.status(HttpStatus.CREATED).body(authorMapper.toDTO(savedAuthor));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  // PUT /authors/{id} - Atualizar autor
  @PutMapping("/{id}")
  @Operation(summary = "Update author", description = "Update an existing author")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Author updated successfully"),
      @ApiResponse(responseCode = "404", description = "Author not found", content = @Content),
      @ApiResponse(responseCode = "400", description = "Invalid author data", content = @Content)
  })
  public ResponseEntity<AuthorDTO> updateAuthor(
      @Parameter(description = "Author ID") @PathVariable Long id,
      @RequestBody AuthorCreateDTO authorCreateDTO) {
    try {
      Author author = authorMapper.toEntity(authorCreateDTO);
      Optional<Author> updatedAuthor = authorService.update(id, author);
      return updatedAuthor.map(authorMapper::toDTO)
          .map(ResponseEntity::ok)
          .orElse(ResponseEntity.notFound().build());
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  // PATCH /authors/{id} - Atualização parcial
  @PatchMapping("/{id}")
  @Operation(summary = "Partially update author", description = "Partially update an existing author")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Author updated successfully"),
      @ApiResponse(responseCode = "404", description = "Author not found", content = @Content),
      @ApiResponse(responseCode = "400", description = "Invalid author data", content = @Content)
  })
  public ResponseEntity<AuthorDTO> patchAuthor(
      @Parameter(description = "Author ID") @PathVariable Long id,
      @RequestBody AuthorCreateDTO authorCreateDTO) {
    try {
      Author author = authorMapper.toEntity(authorCreateDTO);
      Optional<Author> updatedAuthor = authorService.partialUpdate(id, author);
      return updatedAuthor.map(authorMapper::toDTO)
          .map(ResponseEntity::ok)
          .orElse(ResponseEntity.notFound().build());
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  // DELETE /authors/{id} - Deletar autor
  @DeleteMapping("/{id}")
  @Operation(summary = "Delete author", description = "Delete an author by ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Author deleted successfully"),
      @ApiResponse(responseCode = "404", description = "Author not found")
  })
  public ResponseEntity<Void> deleteAuthor(
      @Parameter(description = "Author ID") @PathVariable Long id) {
    boolean deleted = authorService.deleteById(id);
    return deleted ? ResponseEntity.noContent().build()
        : ResponseEntity.notFound().build();
  }
}