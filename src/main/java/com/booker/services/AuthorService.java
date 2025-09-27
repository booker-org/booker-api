package com.booker.services;

import com.booker.entities.Author;
import com.booker.repositories.AuthorRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorService {

  @Autowired
  private AuthorRepository authorRepository;

  public List<Author> findAll() {
    return authorRepository.findAll();
  }

  public Page<Author> findAll(Pageable pageable) {
    return authorRepository.findAll(pageable);
  }

  public Optional<Author> findById(Long id) {
    return authorRepository.findById(id);
  }

  public Author save(Author author) {
    validateAuthor(author);
    return authorRepository.save(author);
  }

  public Optional<Author> update(Long id, Author author) {
    Optional<Author> existingAuthor = authorRepository.findById(id);
    if (existingAuthor.isPresent()) {
      Author authorToUpdate = existingAuthor.get();
      authorToUpdate.setName(author.getName());
      authorToUpdate.setBiography(author.getBiography());
      return Optional.of(authorRepository.save(authorToUpdate));
    }
    return Optional.empty();
  }

  public Optional<Author> partialUpdate(Long id, Author author) {
    Optional<Author> existingAuthor = authorRepository.findById(id);
    if (existingAuthor.isPresent()) {
      Author authorToUpdate = existingAuthor.get();
      
      if (author.getName() != null && !author.getName().trim().isEmpty()) {
        authorToUpdate.setName(author.getName());
      }
      
      if (author.getBiography() != null) {
        authorToUpdate.setBiography(author.getBiography());
      }
      
      return Optional.of(authorRepository.save(authorToUpdate));
    }
    return Optional.empty();
  }

  public boolean deleteById(Long id) {
    if (authorRepository.existsById(id)) {
      authorRepository.deleteById(id);
      return true;
    }
    return false;
  }

  public Optional<Author> findByName(String name) {
    return authorRepository.findByName(name);
  }

  private void validateAuthor(Author author) {
    if (author.getName() == null || author.getName().trim().isEmpty()) {
      throw new IllegalArgumentException("Nome do autor é obrigatório");
    }
  }
}