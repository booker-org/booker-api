package com.booker.services;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.booker.exceptions.CoverException;
import com.booker.exceptions.ResourceNotFoundException;
import com.booker.models.Author;
import com.booker.models.Book;
import com.booker.models.Genre;
import com.booker.repositories.AuthorRepository;
import com.booker.repositories.BookRepository;
import com.booker.repositories.GenreRepository;

@Service
public class BookService {
  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private AuthorRepository authorRepository;

  @Autowired
  private GenreRepository genreRepository;

  @Autowired
  private SupabaseStorageService storageService;

  @Transactional(readOnly = true)
  public Page<Book> findAll(Pageable pageable) { return bookRepository.findAll(pageable); }

  @Transactional(readOnly = true)
  public Optional<Book> findById(Long id) { return bookRepository.findById(id); }

  @Transactional(readOnly = true)
  public Page<Book> findByTitle(String title, Pageable pageable) {
    return bookRepository.findByTitleContainingIgnoreCase(title, pageable);
  }

  @Transactional(readOnly = true)
  public Page<Book> findByAuthor(Long authorId, Pageable pageable) {
    return bookRepository.findByAuthorId(authorId, pageable);
  }

  @Transactional(readOnly = true)
  public Page<Book> searchBooks(String query, Pageable pageable) {
    return bookRepository.findByTitleOrSynopsisContaining(query, pageable);
  }

  @Transactional
  public Book save(Book book, Long authorId, List<Long> genreIds) {
    validateBook(book);

    Author author = authorRepository
      .findById(authorId)
      .orElseThrow(() -> new IllegalArgumentException("ID do autor inválido"))
    ;

    book.setAuthor(author);

    if (genreIds != null && !genreIds.isEmpty()) {
      Set<Genre> genres = new HashSet<>();

      for (Long genreId : genreIds) {
        Genre genre = genreRepository
          .findById(genreId)
          .orElseThrow(() -> new EntityNotFoundException("Gênero não encontrado: " + genreId))
        ;

        genres.add(genre);
      }

      book.setGenres(genres);
    }

    return bookRepository.save(book);
  }

  @Transactional
  public Optional<Book> update(Long id, Book bookData, Long authorId, List<Long> genreIds) {
    return bookRepository.findById(id)
      .map(existingBook -> {
        validateBook(bookData);

        Author author = authorRepository
          .findById(authorId)
          .orElseThrow(() -> new IllegalArgumentException("ID do autor inválido"))
        ;

        existingBook.setAuthor(author);

        if (genreIds != null) {
          Set<Genre> genres = new HashSet<>();

          for (Long genreId : genreIds) {
            Genre genre = genreRepository
              .findById(genreId)
              .orElseThrow(() -> new EntityNotFoundException("Gênero não encontrado: " + genreId))
            ;

            genres.add(genre);
          }

          existingBook.setGenres(genres);
        } else existingBook.getGenres().clear();

        existingBook.setTitle(bookData.getTitle());
        existingBook.setSynopsis(bookData.getSynopsis());
        existingBook.setPageCount(bookData.getPageCount());

        return bookRepository.save(existingBook);
      })
    ;
  }

  @Transactional
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

        if (bookData.getSynopsis() != null) existingBook.setSynopsis(bookData.getSynopsis());

        if (bookData.getPageCount() != null) {
          if (bookData.getPageCount() <= 0) {
            throw new IllegalArgumentException("Número de páginas deve ser maior que zero");
          }

          existingBook.setPageCount(bookData.getPageCount());
        }

        if (bookData.getAuthor() != null) existingBook.setAuthor(bookData.getAuthor());

        if (authorId != null) {
          Author author = authorRepository
            .findById(authorId)
            .orElseThrow(() -> new IllegalArgumentException("ID do autor inválido"))
          ;

          existingBook.setAuthor(author);
        }

        if (genreIds != null) {
          Set<Genre> genres = new HashSet<>();

          for (Long genreId : genreIds) {
            Genre genre = genreRepository
              .findById(genreId)
              .orElseThrow(() -> new ResourceNotFoundException("Gênero não encontrado: " + genreId))
            ;

            genres.add(genre);
          }

          existingBook.setGenres(genres);
        }

        return bookRepository.save(existingBook);
      })
    ;
  }

  @Transactional
  public Optional<Book> updateCover(Long id, MultipartFile coverFile) {
    if (coverFile == null || coverFile.isEmpty()) {
      throw new IllegalArgumentException("Arquivo de capa é obrigatório");
    }

    return bookRepository.findById(id)
      .map(existingBook -> {
        try {
          String newCoverUrl = storageService.uploadOrReplaceCover(existingBook.getCoverUrl(), coverFile);

          existingBook.setCoverUrl(newCoverUrl);

          return bookRepository.save(existingBook);
        } catch (IOException e) { throw new CoverException("Erro no upload da capa: " + e.getMessage()); }
      })
    ;
  }

  @Transactional
  public Optional<Book> removeCover(Long id) {
    return bookRepository.findById(id)
      .map(existingBook -> {
        if (existingBook.getCoverUrl() != null && !existingBook.getCoverUrl().isEmpty()) {
          String fileName = storageService.extractFileNameFromUrl(existingBook.getCoverUrl());

          if (fileName != null) storageService.deleteCover(fileName);

          existingBook.setCoverUrl(null);

          return bookRepository.save(existingBook);
        }

        return existingBook;
      })
    ;
  }

  @Transactional
  public boolean deleteById(Long id) {
    return bookRepository.findById(id)
      .map(book -> {
        // Deleta a capa do Supabase se existir
        if (book.getCoverUrl() != null && !book.getCoverUrl().isEmpty()) {
          try {
            String fileName = storageService.extractFileNameFromUrl(book.getCoverUrl());

            if (fileName != null) storageService.deleteCover(fileName);
          } catch (Exception e) {
            // Log do erro, mas não falha a deleção do livro
            System.err.println("Erro ao deletar capa do livro " + id + ": " + e.getMessage());
          }
        }

        bookRepository.deleteById(id);

        return true;
      })
      .orElse(false)
    ;
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