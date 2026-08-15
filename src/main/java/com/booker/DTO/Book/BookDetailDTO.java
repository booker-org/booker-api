package com.booker.DTO.Book;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.booker.DTO.Author.AuthorDTO;
import com.booker.DTO.Genre.GenreDTO;

public record BookDetailDTO(
  UUID id,
  String title,
  String synopsis,
  Integer pageCount,
  Short releaseYear,
  AuthorDTO author,
  List<GenreDTO> genres,
  String coverUrl,
  BigDecimal rating,
  Integer ratingsCount,
  LocalDateTime createdAt,
  LocalDateTime updatedAt
) {}