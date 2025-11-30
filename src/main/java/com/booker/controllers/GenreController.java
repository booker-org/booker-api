package com.booker.controllers;

import java.util.List;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.booker.DTO.Genre.GenreCreateDTO;
import com.booker.DTO.Genre.GenreDTO;
import com.booker.mappers.GenreMapper;
import com.booker.models.Genre;
import com.booker.services.GenreService;

@RestController @RequestMapping("/genres")
@Tag(name = "Genres", description = "Genre management endpoints")
public class GenreController {
  @Autowired
  private GenreService genreService;

  @Autowired
  private GenreMapper genreMapper;

  @GetMapping
  @Operation(summary = "Get all genres", description = "Get paginated list of all genres")
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
      .orElse(ResponseEntity.notFound().build())
    ;
  }

  @PostMapping
  @Operation(summary = "Create new genre", description = "Create a new genre")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Genre created successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid genre data")
  })
  public ResponseEntity<GenreDTO> createGenre(@RequestBody GenreCreateDTO genreCreateDTO) {
    Genre genre = genreMapper.toEntity(genreCreateDTO);
    Genre savedGenre = genreService.save(genre);

    return ResponseEntity.status(HttpStatus.CREATED).body(genreMapper.toDTO(savedGenre));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update genre", description = "Update an existing genre")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Genre updated successfully"),
    @ApiResponse(responseCode = "404", description = "Genre not found", content = @Content),
    @ApiResponse(responseCode = "400", description = "Invalid genre data", content = @Content)
  })
  public ResponseEntity<GenreDTO> updateGenre(
    @Parameter(description = "Genre ID") @PathVariable UUID id,
    @RequestBody GenreCreateDTO genreCreateDTO
  ) {
    Genre genre = genreMapper.toEntity(genreCreateDTO);
    Optional<Genre> updatedGenre = genreService.update(id, genre);

    return updatedGenre.map(genreMapper::toDTO)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build())
    ;
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete genre", description = "Delete a genre by ID")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "Genre deleted successfully"),
    @ApiResponse(responseCode = "404", description = "Genre not found")
  })
  public ResponseEntity<Void> deleteGenre(@Parameter(description = "Genre ID") @PathVariable UUID id) {
    boolean deleted = genreService.deleteById(id);

    return deleted
      ? ResponseEntity.noContent().build()
      : ResponseEntity.notFound().build()
    ;
  }
}