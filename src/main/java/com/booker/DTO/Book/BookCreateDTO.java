package com.booker.DTO.Book;

import jakarta.validation.constraints.*;

import java.util.List;
import java.util.UUID;

public record BookCreateDTO(
  @NotBlank(message = "Title is required") @Size(min = 2, max = 200,
    message = "Title must be between 2 and 200 characters") String title,
  
  @Size(max = 5000, message = "Synopsis must not exceed 5000 characters") String synopsis,
  
  @Min(value = 1, message = "Page count must be at least 1") @Max(value = 50000,
    message = "Page count must not exceed 50000") Integer pageCount,
  
  @NotNull(message = "Author ID is required") UUID authorId,
  
  List<UUID> genreIds) {
}