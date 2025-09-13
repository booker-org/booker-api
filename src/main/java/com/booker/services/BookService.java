package com.booker.services;

import com.booker.entities.Book;
import com.booker.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookService {
    
    @Autowired
    private BookRepository bookRepository;
    
    public Page<Book> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }
    
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }
    
    public Book save(Book book) {
        validateBook(book);
        return bookRepository.save(book);
    }
    
    public Optional<Book> update(Long id, Book bookData) {
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
        
        if (book.getAuthor() == null) {
            throw new IllegalArgumentException("ID do autor deve ser válido");
        }
    }
}
