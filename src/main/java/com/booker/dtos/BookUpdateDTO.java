package com.booker.dtos;

public record BookUpdateDTO(String title,
        String synopsis,
        Integer pageCount,
        Long authorId,
        String coverUrl) {
}
