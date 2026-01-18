package com.booker.mappers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.booker.DTO.Author.AuthorCreateDTO;
import com.booker.DTO.Author.AuthorDTO;
import com.booker.models.Author;

@Component
public class AuthorMapper {
  public AuthorDTO toDTO(Author author) {
    if (author == null) return null;

    return new AuthorDTO(
      author.getId(),
      author.getName(),
      author.getBiography()
    );
  }

  public Author toEntity(AuthorCreateDTO authorCreateDTO) {
    if (authorCreateDTO == null) return null;

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

  public Page<AuthorDTO> toDTOPage(Page<Author> authors) {
    return authors.map(this::toDTO);
  }
}