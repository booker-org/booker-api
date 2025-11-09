package com.booker.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.booker.models.Author;
import com.booker.repositories.AuthorRepository;

@Service
public class AuthorService {
  @Autowired
  private AuthorRepository repository;

  @Transactional(readOnly = true)
  public List<Author> findAll() { return repository.findAll(); }

  @Transactional(readOnly = true)
  public Page<Author> findAll(Pageable pageable) { return repository.findAll(pageable); }

  @Transactional(readOnly = true)
  public Optional<Author> findById(UUID id) { return repository.findById(id); }

  @Transactional(readOnly = true)
  public Optional<Author> findByName(String name) { return repository.findByName(name); }

  @Transactional
  public Author save(Author author) {
    validateAuthor(author);

    return repository.save(author);
  }

  @Transactional
  public Optional<Author> update(UUID id, Author author) {
    Optional<Author> existingAuthor = repository.findById(id);

    if (existingAuthor.isPresent()) {
      Author authorToUpdate = existingAuthor.get();

      authorToUpdate.setName(author.getName());
      authorToUpdate.setBiography(author.getBiography());

      return Optional.of(repository.save(authorToUpdate));
    }

    return Optional.empty();
  }

  @Transactional
  public Optional<Author> partialUpdate(UUID id, Author author) {
    Optional<Author> existingAuthor = repository.findById(id);

    if (existingAuthor.isPresent()) {
      Author authorToUpdate = existingAuthor.get();

      if (author.getName() != null && !author.getName().trim().isEmpty()) {
        authorToUpdate.setName(author.getName());
      }

      if (author.getBiography() != null) {
        authorToUpdate.setBiography(author.getBiography());
      }

      return Optional.of(repository.save(authorToUpdate));
    }

    return Optional.empty();
  }

  @Transactional
  public boolean deleteById(UUID id) {
    if (repository.existsById(id)) {
      repository.deleteById(id);

      return true;
    }

    return false;
  }

  private void validateAuthor(Author author) {
    if (author.getName() == null || author.getName().trim().isEmpty()) {
      throw new IllegalArgumentException("Nome do autor é obrigatório");
    }
  }
}