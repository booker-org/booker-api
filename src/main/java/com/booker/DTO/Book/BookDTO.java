package com.booker.DTO.Book;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record BookDTO(
  UUID id,
  String title,
  String synopsis,
  Integer pageCount,
  Short releaseYear,
  String authorName,
  List<String> genres,
  String coverUrl,
  BigDecimal rating,
  Integer ratingsCount,
  LocalDateTime createdAt,
  LocalDateTime updatedAt
) {}