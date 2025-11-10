package com.booker.DTO.Author;

import java.util.List;
import java.util.UUID;

public record BookCreateDTO(
  String title,
  String synopsis,
  Integer pageCount,
  UUID authorId,
  List<UUID> genreIds
) {}