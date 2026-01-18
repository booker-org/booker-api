package com.booker.DTO.Author;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthorCreateDTO(
  @NotBlank(message = "Name is required")
  @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
  String name,

  @Size(min = 10, max = 5000, message = "Biography must be between 10 and 5000 characters")
  String biography
) {}