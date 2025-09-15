package com.booker.mappers;

import com.booker.dtos.GenreDTO;
import com.booker.entities.Genre;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GenreMapper {

  public GenreDTO toDTO(Genre genre) {
    if (genre == null)
      return null;

    return new GenreDTO(
        genre.getId(),
        genre.getName());
  }

  public List<GenreDTO> toDTOList(List<Genre> genres) {
    return genres.stream()
        .map(this::toDTO)
        .toList();
  }
}