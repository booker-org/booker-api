package com.booker.dtos;

import java.time.LocalDateTime;
import java.util.List;

public record BookDetailDTO(
        Long id,
        String title,
        String synopsis,
        Integer pageCount,
        AuthorDTO author,
        List<GenreDTO> genres,
        String coverUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

}
