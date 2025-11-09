package com.booker.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.booker.models.Genre;
import com.booker.repositories.GenreRepository;

@Service
public class GenreService {
  @Autowired
  private GenreRepository repository;

  @Transactional(readOnly = true)
  public List<Genre> findAll() { return repository.findAll(); }

  @Transactional(readOnly = true)
  public Page<Genre> findAll(Pageable pageable) { return repository.findAll(pageable); }

  @Transactional(readOnly = true)
  public Optional<Genre> findById(Long id) { return repository.findById(id); }

  @Transactional(readOnly = true)
  public Optional<Genre> findByName(String name) { return repository.findByName(name); }

  @Transactional
  public Genre save(Genre genre) {
    validateGenre(genre);

    return repository.save(genre);
  }

  @Transactional
  public Optional<Genre> update(Long id, Genre genre) {
    Optional<Genre> existingGenre = repository.findById(id);

    if (existingGenre.isPresent()) {
      Genre genreToUpdate = existingGenre.get();

      genreToUpdate.setName(genre.getName());

      return Optional.of(repository.save(genreToUpdate));
    }

    return Optional.empty();
  }

  @Transactional
  public boolean deleteById(Long id) {
    if (repository.existsById(id)) {
      repository.deleteById(id);

      return true;
    }

    return false;
  }

  private void validateGenre(Genre genre) {
    if (genre.getName() == null || genre.getName().trim().isEmpty()) {
      throw new IllegalArgumentException("Nome do gênero é obrigatório");
    }
  }
}