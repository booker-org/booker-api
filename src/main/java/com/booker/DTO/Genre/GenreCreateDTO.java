package com.booker.DTO.Genre;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GenreCreateDTO(
  @NotBlank(message = "Genre name is required")
  @Size(min = 2, max = 100, message = "Genre name must be between 2 and 100 characters")
  String name
) {}