package com.booker.DTO.Author;

import java.util.UUID;

public record AuthorDTO(
  UUID id,
  String name,
  String biography) {
}