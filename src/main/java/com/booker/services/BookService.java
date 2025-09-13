package com.booker.services;

import com.booker.models.Book;
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
        return bookRepository.save(book);
    }
    
    public Optional<Book> update(Long id, Book bookData) {
        return bookRepository.findById(id)
                .map(existingBook -> {
                    existingBook.setTitle(bookData.getTitle());
                    existingBook.setSynopsis(bookData.getSynopsis());
                    existingBook.setPageCount(bookData.getPageCount());
                    existingBook.setAuthorId(bookData.getAuthorId());
                    existingBook.setCoverUrl(bookData.getCoverUrl());
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
}
