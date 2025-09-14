package com.booker.dtos;

import java.time.LocalDateTime;
import java.util.List;

public record BookDTO(
        Long id,
        String title,
        String synopsis,
        Integer pageCount,
        String authorName,
        List<String> genres,
        String coverUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

}
