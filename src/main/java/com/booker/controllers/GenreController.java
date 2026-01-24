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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.booker.DTO.Genre.GenreCreateDTO;
import com.booker.DTO.Genre.GenreDTO;
import com.booker.mappers.GenreMapper;
import com.booker.models.Genre;
import com.booker.services.GenreService;

import static com.booker.constants.Auth.ADMIN_ROLE;
import static com.booker.constants.Auth.ADMIN_AUTHORIZATION;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
@Tag(name = "Genres", description = "Genre management endpoints")
public class GenreController {
  private final GenreService genreService;
  private final GenreMapper genreMapper;

  @GetMapping
  @Operation(summary = "Get all genres", description = "Get paginated list of all genres (max 100 per page)")
  public ResponseEntity<Page<GenreDTO>> getAllGenres(
    @ParameterObject @PageableDefault(size = 10, sort = "name") Pageable pageable
  ) {
    Page<Genre> genres = genreService.findAll(pageable);
    Page<GenreDTO> genreDTOs = genreMapper.toDTOPage(genres);

    return ResponseEntity.ok(genreDTOs);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get genre by ID", description = "Get a specific genre by its ID")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Genre found"),
    @ApiResponse(responseCode = "404", description = "Genre not found")
  })
  public ResponseEntity<GenreDTO> getGenreById(@Parameter(description = "Genre ID") @PathVariable UUID id) {
    Optional<Genre> genre = genreService.findById(id);

    return genre.map(genreMapper::toDTO)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping @PreAuthorize(ADMIN_AUTHORIZATION)
  @Operation(summary = "Create new genre - " + ADMIN_ROLE, description = "Create a new genre")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Gênero criado com sucesso"),
    @ApiResponse(responseCode = "400", description = "Dados de gênero inválidos")
  })
  public ResponseEntity<GenreDTO> createGenre(@Valid @RequestBody GenreCreateDTO genreCreateDTO) {
    Genre genre = genreMapper.toEntity(genreCreateDTO);
    Genre savedGenre = genreService.save(genre);

    return ResponseEntity.status(HttpStatus.CREATED).body(genreMapper.toDTO(savedGenre));
  }

  @PutMapping("/{id}") @PreAuthorize(ADMIN_AUTHORIZATION)
  @Operation(summary = "Update genre - " + ADMIN_ROLE, description = "Update an existing genre")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Gênero atualizado com sucesso"),
    @ApiResponse(responseCode = "404", description = "Gênero não encontrado", content = @Content),
    @ApiResponse(responseCode = "400", description = "Dados de gênero inválidos", content = @Content)
  })
  public ResponseEntity<GenreDTO> updateGenre(
    @Parameter(description = "Genre ID") @PathVariable UUID id,
    @Valid @RequestBody GenreCreateDTO genreCreateDTO
  ) {
    Genre genre = genreMapper.toEntity(genreCreateDTO);
    Optional<Genre> updatedGenre = genreService.update(id, genre);

    return updatedGenre.map(genreMapper::toDTO)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}") @PreAuthorize(ADMIN_AUTHORIZATION)
  @Operation(summary = "Delete genre - " + ADMIN_ROLE, description = "Delete a genre by ID")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "Gênero excluído com sucesso"),
    @ApiResponse(responseCode = "404", description = "Gênero não encontrado")
  })
  public ResponseEntity<Void> deleteGenre(@Parameter(description = "Genre ID") @PathVariable UUID id) {
    boolean deleted = genreService.deleteById(id);

    return deleted
      ? ResponseEntity.noContent().build()
      : ResponseEntity.notFound().build();
  }
}