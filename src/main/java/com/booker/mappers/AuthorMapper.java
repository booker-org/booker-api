package com.booker.mappers;

import com.booker.dtos.AuthorDTO;
import com.booker.dtos.AuthorCreateDTO;
import com.booker.entities.Author;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthorMapper {

  public AuthorDTO toDTO(Author author) {
    if (author == null)
      return null;

    return new AuthorDTO(
        author.getId(),
        author.getName(),
        author.getBiography());
  }

  public Author toEntity(AuthorCreateDTO authorCreateDTO) {
    if (authorCreateDTO == null)
      return null;

    Author author = new Author();
    author.setName(authorCreateDTO.name());
    author.setBiography(authorCreateDTO.biography());
    return author;
  }

  public List<AuthorDTO> toDTOList(List<Author> authors) {
    return authors.stream()
        .map(this::toDTO)
        .toList();
  }
}