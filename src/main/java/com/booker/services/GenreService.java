package com.booker.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.booker.models.Genre;
import com.booker.repositories.GenreRepository;

@Service
public class GenreService {
  @Autowired
  private GenreRepository genreRepository;

  public List<Genre> findAll() { return genreRepository.findAll(); }

  public Page<Genre> findAll(Pageable pageable) { return genreRepository.findAll(pageable); }

  public Optional<Genre> findById(Long id) { return genreRepository.findById(id); }

  public Genre save(Genre genre) {
    validateGenre(genre);

    return genreRepository.save(genre);
  }

  public Optional<Genre> update(Long id, Genre genre) {
    Optional<Genre> existingGenre = genreRepository.findById(id);

    if (existingGenre.isPresent()) {
      Genre genreToUpdate = existingGenre.get();

      genreToUpdate.setName(genre.getName());

      return Optional.of(genreRepository.save(genreToUpdate));
    }

    return Optional.empty();
  }

  public boolean deleteById(Long id) {
    if (genreRepository.existsById(id)) {
      genreRepository.deleteById(id);

      return true;
    }

    return false;
  }

  public Optional<Genre> findByName(String name) { return genreRepository.findByName(name); }

  private void validateGenre(Genre genre) {
    if (genre.getName() == null || genre.getName().trim().isEmpty()) {
      throw new IllegalArgumentException("Nome do gênero é obrigatório");
    }
  }
}