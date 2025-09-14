package com.booker.mappers;

import com.booker.dtos.AuthorDTO;
import com.booker.entities.Author;
import org.springframework.stereotype.Component;

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
}