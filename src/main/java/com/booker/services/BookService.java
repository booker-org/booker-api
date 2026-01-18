package com.booker.services;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.booker.DTO.Book.BookDTO;
import com.booker.DTO.Book.BookDetailDTO;
import com.booker.exceptions.CoverException;
import com.booker.exceptions.ResourceNotFoundException;
import com.booker.mappers.BookMapper;
import com.booker.models.Author;
import com.booker.models.Book;
import com.booker.models.Genre;
import com.booker.repositories.BookRepository;

@Service @RequiredArgsConstructor
public class BookService {
  private final BookRepository bookRepository;
  private final BookMapper bookMapper;
  private final AuthorService authorService;
  private final GenreService genreService;
  private final SupabaseStorageService storageService;

  @Transactional(readOnly = true)
  public Page<BookDTO> findAll(Pageable pageable) {
    return bookRepository.findAll(pageable)
      .map(bookMapper::toDTO);
  }

  @Transactional(readOnly = true)
  public BookDetailDTO findById(UUID id) {
    return bookRepository.findById(id)
      .map(bookMapper::toDetailDTO)
      .orElseThrow(() -> new ResourceNotFoundException("Livro não encontrado")
    );
  }

  @Transactional(readOnly = true)
  public Page<BookDTO> findByTitle(String title, Pageable pageable) {
    return bookRepository.findByTitleContainingIgnoreCase(title, pageable)
      .map(bookMapper::toDTO);
  }

  @Transactional(readOnly = true)
  public Page<BookDTO> findByAuthor(UUID authorId, Pageable pageable) {
    return bookRepository.findByAuthorId(authorId, pageable)
      .map(bookMapper::toDTO);
  }

  @Transactional(readOnly = true)
  public Page<BookDTO> searchBooks(String query, Pageable pageable) {
    return bookRepository.findByTitleOrSynopsisContaining(query, pageable)
      .map(bookMapper::toDTO);
  }

  @Transactional
  public BookDetailDTO save(Book book, UUID authorId, List<UUID> genreIds) {
    validateBook(book);

    Author author = authorService
      .findById(authorId)
      .orElseThrow(() -> new IllegalArgumentException("ID do autor inválido")
    );

    book.setAuthor(author);

    if (genreIds != null && !genreIds.isEmpty()) {
      Set<Genre> genres = new HashSet<>();

      for (UUID genreId : genreIds) {
        Genre genre = genreService
          .findById(genreId)
          .orElseThrow(() -> new EntityNotFoundException("Gênero não encontrado: " + genreId)
        );

        genres.add(genre);
      }

      book.setGenres(genres);
    }

    Book savedBook = bookRepository.save(book);

    return bookMapper.toDetailDTO(savedBook);
  }

  @Transactional
  public Optional<BookDetailDTO> update(UUID id, Book bookData, UUID authorId, List<UUID> genreIds) {
    return bookRepository.findById(id)
      .map(existingBook -> {
        validateBook(bookData);

        Author author = authorService
          .findById(authorId)
          .orElseThrow(() -> new IllegalArgumentException("ID do autor inválido")
        );

        existingBook.setAuthor(author);

        if (genreIds != null) {
          Set<Genre> genres = new HashSet<>();

          for (UUID genreId : genreIds) {
            Genre genre = genreService
              .findById(genreId)
              .orElseThrow(() -> new EntityNotFoundException("Gênero não encontrado: " + genreId)
            );

            genres.add(genre);
          }

          existingBook.setGenres(genres);
        } else existingBook.getGenres().clear();

        existingBook.setTitle(bookData.getTitle());
        existingBook.setSynopsis(bookData.getSynopsis());
        existingBook.setPageCount(bookData.getPageCount());

        Book updatedBook = bookRepository.save(existingBook);

        return bookMapper.toDetailDTO(updatedBook);
      }
    );
  }

  @Transactional
  public Optional<BookDetailDTO> partialUpdate(UUID id, Book bookData, UUID authorId, List<UUID> genreIds) {
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
          Author author = authorService
            .findById(authorId)
            .orElseThrow(() -> new IllegalArgumentException("ID do autor inválido")
          );

          existingBook.setAuthor(author);
        }

        if (genreIds != null) {
          Set<Genre> genres = new HashSet<>();

          for (UUID genreId : genreIds) {
            Genre genre = genreService
              .findById(genreId)
              .orElseThrow(() -> new ResourceNotFoundException("Gênero não encontrado: " + genreId)
            );

            genres.add(genre);
          }

          existingBook.setGenres(genres);
        }

        Book updatedBook = bookRepository.save(existingBook);

        return bookMapper.toDetailDTO(updatedBook);
      }
    );
  }

  @Transactional
  public Optional<BookDetailDTO> updateCover(UUID id, MultipartFile coverFile) {
    if (coverFile == null || coverFile.isEmpty()) {
      throw new IllegalArgumentException("Arquivo de capa é obrigatório");
    }

    return bookRepository.findById(id)
      .map(existingBook -> {
        try {
          String newCoverUrl = storageService.uploadOrReplaceCover(existingBook.getCoverUrl(), coverFile);

          existingBook.setCoverUrl(newCoverUrl);

          Book updatedBook = bookRepository.save(existingBook);

          return bookMapper.toDetailDTO(updatedBook);
        } catch (IOException e) {
          throw new CoverException("Erro no upload da capa: " + e.getMessage());
        }
      }
    );
  }

  @Transactional
  public boolean removeCover(UUID id) {
    return bookRepository.findById(id)
      .map(existingBook -> {
        if (existingBook.getCoverUrl() != null && !existingBook.getCoverUrl().isEmpty()) {
          String fileName = storageService.extractFileNameFromUrl(existingBook.getCoverUrl());

          if (fileName != null) storageService.deleteCover(fileName);

          existingBook.setCoverUrl(null);
          bookRepository.save(existingBook);

          return true;
        }

        return false;
      })
      .orElse(false)
    ;
  }

  @Transactional
  public boolean deleteById(UUID id) {
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