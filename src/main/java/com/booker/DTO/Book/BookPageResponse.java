package com.booker.DTO.Book;

import java.util.List;

public record BookPageResponse(
  List<BookDTO> content,
  int page,
  int size,
  long totalElements,
  int totalPages
) {}