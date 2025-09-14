package com.booker.dtos;

import java.util.List;

public record BookCreateDTO(
    String title,
    String synopsis,
    Integer pageCount,
    Long authorId,
    List<Long> genreIds,
    String coverUrl) {
}
