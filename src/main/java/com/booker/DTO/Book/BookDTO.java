package com.booker.DTO.Book;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record BookDTO(
  UUID id,
  String title,
  String synopsis,
  Integer pageCount,
  String authorName,
  List<String> genres,
  String coverUrl,
  LocalDateTime createdAt,
  LocalDateTime updatedAt
) {}