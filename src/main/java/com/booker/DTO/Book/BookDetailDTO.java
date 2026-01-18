package com.booker.DTO.Book;

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
  AuthorDTO author,
  List<GenreDTO> genres,
  String coverUrl,
  LocalDateTime createdAt,
  LocalDateTime updatedAt
) {}