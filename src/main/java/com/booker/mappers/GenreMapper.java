package com.booker.mappers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.booker.DTO.Genre.GenreCreateDTO;
import com.booker.DTO.Genre.GenreDTO;
import com.booker.models.Genre;

@Component
public class GenreMapper {
  public GenreDTO toDTO(Genre genre) {
    if (genre == null) return null;

    return new GenreDTO(
      genre.getId(),
      genre.getName()
    );
  }

  public Genre toEntity(GenreCreateDTO genreCreateDTO) {
    if (genreCreateDTO == null) return null;

    Genre genre = new Genre();

    genre.setName(genreCreateDTO.name());

    return genre;
  }

  public List<GenreDTO> toDTOList(List<Genre> genres) {
    return genres.stream()
      .map(this::toDTO)
      .toList()
    ;
  }

  public Page<GenreDTO> toDTOPage(Page<Genre> genres) {
    return genres.map(this::toDTO);
  }
}