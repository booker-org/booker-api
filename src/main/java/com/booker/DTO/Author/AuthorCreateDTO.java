package com.booker.DTO.Author;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthorCreateDTO(
  @NotBlank(message = "Name is required") @Size(min = 2, max = 100,
    message = "Name must be between 2 and 100 characters") String name,

  @Size(min = 10, max = 2000, message = "Biography must be between 10 and 2000 characters") String biography) {
}