package com.booker.services;

import com.booker.entities.Author;
import com.booker.entities.Book;
import com.booker.entities.Genre;
import com.booker.repositories.AuthorRepository;
import com.booker.repositories.BookRepository;
import com.booker.repositories.GenreRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class BookService {

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private AuthorRepository authorRepository;

  @Autowired
  private GenreRepository genreRepository;

  public Page<Book> findAll(Pageable pageable) {
    return bookRepository.findAll(pageable);
  }

  public Optional<Book> findById(Long id) {
    return bookRepository.findById(id);
  }

  public Book save(Book book, Long authorId, List<Long> genreIds) {

    validateBook(book);

    Author author = authorRepository.findById(authorId)
        .orElseThrow(() -> new IllegalArgumentException("ID do autor inválido"));
    book.setAuthor(author);

    if (genreIds != null && !genreIds.isEmpty()) {
      Set<Genre> genres = new HashSet<>();
      for (Long genreId : genreIds) {
        Genre genre = genreRepository.findById(genreId)
            .orElseThrow(() -> new EntityNotFoundException("Gênero não encontrado: " + genreId));
        genres.add(genre);
      }
      book.setGenres(genres);
    }

    return bookRepository.save(book);
  }

  public Optional<Book> update(Long id, Book bookData, Long authorId, List<Long> genreIds) {
    return bookRepository.findById(id)
        .map(existingBook -> {

          validateBook(existingBook);

          Author author = authorRepository.findById(authorId)
              .orElseThrow(() -> new IllegalArgumentException("ID do autor inválido"));
          existingBook.setAuthor(author);

          if (genreIds != null) {
            Set<Genre> genres = new HashSet<>();
            for (Long genreId : genreIds) {
              Genre genre = genreRepository.findById(genreId)
                  .orElseThrow(
                      () -> new EntityNotFoundException("Gênero não encontrado: " + genreId));
              genres.add(genre);
            }
            existingBook.setGenres(genres);
          } else {
            existingBook.getGenres().clear();
          }
          existingBook.setTitle(bookData.getTitle());
          existingBook.setSynopsis(bookData.getSynopsis());
          existingBook.setPageCount(bookData.getPageCount());
          existingBook.setCoverUrl(bookData.getCoverUrl());

          return bookRepository.save(existingBook);
        });
  }

  public Optional<Book> partialUpdate(Long id, Book bookData, Long authorId, List<Long> genreIds) {
    return bookRepository.findById(id)
        .map(existingBook -> {
          // Validar apenas se novos dados são fornecidos
          if (bookData.getTitle() != null) {
            if (bookData.getTitle().length() < 2 || bookData.getTitle().length() > 200) {
              throw new IllegalArgumentException("Título deve ter entre 2 e 200 caracteres");
            }
            existingBook.setTitle(bookData.getTitle());
          }
          if (bookData.getSynopsis() != null) {
            existingBook.setSynopsis(bookData.getSynopsis());
          }
          if (bookData.getPageCount() != null) {
            if (bookData.getPageCount() <= 0) {
              throw new IllegalArgumentException("Número de páginas deve ser maior que zero");
            }
            existingBook.setPageCount(bookData.getPageCount());
          }
          if (bookData.getAuthor() != null) {
            existingBook.setAuthor(bookData.getAuthor());
          }
          if (bookData.getCoverUrl() != null) {
            existingBook.setCoverUrl(bookData.getCoverUrl());
          }

          if (authorId != null) {
            Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("ID do autor inválido"));
            existingBook.setAuthor(author);
          }

          if (genreIds != null) {
            Set<Genre> genres = new HashSet<>();
            for (Long genreId : genreIds) {
              Genre genre = genreRepository.findById(genreId)
                  .orElseThrow(
                      () -> new EntityNotFoundException("Gênero não encontrado: " + genreId));
              genres.add(genre);
            }
            existingBook.setGenres(genres);
          }
          return bookRepository.save(existingBook);
        });
  }

  public boolean deleteById(Long id) {
    if (bookRepository.existsById(id)) {
      bookRepository.deleteById(id);
      return true;
    }
    return false;
  }

  public Page<Book> findByTitle(String title, Pageable pageable) {
    return bookRepository.findByTitleContainingIgnoreCase(title, pageable);
  }

  public Page<Book> findByAuthor(Long authorId, Pageable pageable) {
    return bookRepository.findByAuthorId(authorId, pageable);
  }

  public Page<Book> searchBooks(String query, Pageable pageable) {
    return bookRepository.findByTitleOrSynopsisContaining(query, pageable);
  }

  private void validateBook(Book book) {
    if (book == null) {
      throw new IllegalArgumentException("Livro não pode ser nulo");
    }

    if (book.getTitle() == null) {
      throw new IllegalArgumentException("Título é obrigatório");
    }

    if (book.getTitle().length() < 2 || book.getTitle().length() > 200) {
      throw new IllegalArgumentException("Título deve ter entre 2 e 200 caracteres");
    }

    if (book.getPageCount() == null || book.getPageCount() <= 0) {
      throw new IllegalArgumentException("Número de páginas deve ser maior que zero");
    }
  }
}
