package com.booker.services;

import com.booker.models.Book;
import com.booker.repositories.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book testBook;

    @BeforeEach
    void setUp() {
        testBook = createBaseBook();
        testBook.setId(1L);
    }

    private Book createBaseBook() {
        Book book = new Book();
        book.setTitle("Dom Casmurro");
        book.setSynopsis("A obra narra a vida de Bento Santiago...");
        book.setPageCount(256);
        book.setAuthorId(1L);
        book.setCoverUrl("https://example.com/dom-casmurro.jpg");
        return book;
    }

    // ========== FIND BY ID TESTS ==========

    @Test
    void findById_ShouldReturnBook_WhenBookExists() {
        // Given - Existing ID
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        // When
        Optional<Book> result = bookService.findById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testBook.getId(), result.get().getId());
        assertEquals(testBook.getTitle(), result.get().getTitle());
        verify(bookRepository).findById(1L);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenBookNotExists() {
        // Given - Non-existent ID
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Book> result = bookService.findById(999L);

        // Then
        assertFalse(result.isPresent());
        verify(bookRepository).findById(999L);
    }

    // ========== FIND ALL TESTS ==========

    @Test
    void findAll_ShouldReturnPageOfBooks() {
        // Given - Some books
        List<Book> books = Arrays.asList(testBook, createAnotherBook());
        Page<Book> bookPage = new PageImpl<>(books);
        Pageable pageable = PageRequest.of(0, 10);
        
        when(bookRepository.findAll(pageable)).thenReturn(bookPage);

        // When
        Page<Book> result = bookService.findAll(pageable);

        // Then
        assertEquals(2, result.getContent().size());
        assertEquals(testBook.getTitle(), result.getContent().get(0).getTitle());
        verify(bookRepository).findAll(pageable);
    }

    @Test
    void findAll_ShouldReturnEmptyPage_WhenNoBooksExist() {
        // Given - No books
        Page<Book> emptyPage = new PageImpl<>(Arrays.asList());
        Pageable pageable = PageRequest.of(0, 10);
        
        when(bookRepository.findAll(pageable)).thenReturn(emptyPage);

        // When
        Page<Book> result = bookService.findAll(pageable);

        // Then
        assertEquals(0, result.getContent().size());
        verify(bookRepository).findAll(pageable);
    }

    // ========== SAVE TESTS ==========

    @Test
    void save_ShouldReturnSavedBook_WhenValidBook() {
        // Given - Valid book
        Book newBook = createBaseBook();
        Book savedBook = createBaseBook();
        savedBook.setId(1L);
        
        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        // When
        Book result = bookService.save(newBook);

        // Then
        assertNotNull(result.getId());
        assertEquals(newBook.getTitle(), result.getTitle());
        assertEquals(newBook.getSynopsis(), result.getSynopsis());
        verify(bookRepository).save(newBook);
    }

    @Test
    void save_ShouldThrowException_WhenBookIsNull() {
        // Given - Null book

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            bookService.save(null);
        });
        
        verify(bookRepository, never()).save(any());
    }

    @Test
    void save_ShouldThrowException_WhenTitleIsNull() {
        // Given - Null title
        Book invalidBook = createBaseBook();
        invalidBook.setTitle(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookService.save(invalidBook);
        });
        
        assertEquals("Título é obrigatório", exception.getMessage());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void save_ShouldThrowException_WhenTitleIsTooShort() {
        // Given - Title too short
        Book invalidBook = createBaseBook();
        invalidBook.setTitle("A");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookService.save(invalidBook);
        });
        
        assertEquals("Título deve ter entre 2 e 200 caracteres", exception.getMessage());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void save_ShouldThrowException_WhenTitleIsTooLong() {
        // Given - Title exceeding max length
        Book invalidBook = createBaseBook();
        invalidBook.setTitle("A".repeat(201));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookService.save(invalidBook);
        });
        
        assertEquals("Título deve ter entre 2 e 200 caracteres", exception.getMessage());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void save_ShouldThrowException_WhenPageCountIsNegative() {
        // Given - Negative page count
        Book invalidBook = createBaseBook();
        invalidBook.setPageCount(-10);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookService.save(invalidBook);
        });
        
        assertEquals("Número de páginas deve ser maior que zero", exception.getMessage());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void save_ShouldThrowException_WhenAuthorIdIsNull() {
        // Given - Null authorId
        Book invalidBook = createBaseBook();
        invalidBook.setAuthorId(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookService.save(invalidBook);
        });
        
        assertEquals("ID do autor deve ser válido", exception.getMessage());
        verify(bookRepository, never()).save(any());
    }

    // ========== UPDATE TESTS ==========

    @Test
    void update_ShouldReturnUpdatedBook_WhenBookExists() {
        // Given - Existing ID
        Long bookId = 1L;
        Book updateRequest = createBaseBook();
        updateRequest.setTitle("Título Atualizado");
        updateRequest.setPageCount(300);

        Book existingBook = createBaseBook();
        existingBook.setId(bookId);

        Book updatedBook = createBaseBook();
        updatedBook.setId(bookId);
        updatedBook.setTitle("Título Atualizado");
        updatedBook.setPageCount(300);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);

        // When
        Optional<Book> result = bookService.update(bookId, updateRequest);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Título Atualizado", result.get().getTitle());
        assertEquals(300, result.get().getPageCount());
        verify(bookRepository).findById(bookId);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void update_ShouldReturnEmpty_WhenBookNotExists() {
        // Given - Non-existent ID
        Long nonExistentId = 999L;
        Book updateRequest = createBaseBook();
        
        when(bookRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When
        Optional<Book> result = bookService.update(nonExistentId, updateRequest);

        // Then
        assertFalse(result.isPresent());
        verify(bookRepository).findById(nonExistentId);
        verify(bookRepository, never()).save(any());
    }

    @Test
    void update_ShouldOnlyUpdateNonNullFields() {
        // Given - Partial update (only title)
        Long bookId = 1L;
        Book updateRequest = new Book();
        updateRequest.setTitle("Novo Título");

        Book existingBook = createBaseBook();
        existingBook.setId(bookId);

        Book expectedUpdatedBook = createBaseBook();
        expectedUpdatedBook.setId(bookId);
        expectedUpdatedBook.setTitle("Novo Título");

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenReturn(expectedUpdatedBook);

        // When
        Optional<Book> result = bookService.update(bookId, updateRequest);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Novo Título", result.get().getTitle());
        // Campos originais devem ser mantidos
        assertEquals("A obra narra a vida de Bento Santiago...", result.get().getSynopsis());
        assertEquals(256, result.get().getPageCount());
        verify(bookRepository).save(any(Book.class));
    }

    // ========== DELETE TESTS ==========

    @Test
    void deleteById_ShouldReturnTrue_WhenBookExists() {
        // Given - Existing ID
        when(bookRepository.existsById(1L)).thenReturn(true);

        // When
        boolean result = bookService.deleteById(1L);

        // Then
        assertTrue(result);
        verify(bookRepository).existsById(1L);
        verify(bookRepository).deleteById(1L);
    }

    @Test
    void deleteById_ShouldReturnFalse_WhenBookNotExists() {
        // Given - Non-existent ID
        when(bookRepository.existsById(999L)).thenReturn(false);

        // When
        boolean result = bookService.deleteById(999L);

        // Then
        assertFalse(result);
        verify(bookRepository).existsById(999L);
        verify(bookRepository, never()).deleteById(any());
    }

    // ========== HELPER METHODS ==========

    private Book createAnotherBook() {
        Book book = new Book();
        book.setId(2L);
        book.setTitle("O Cortiço");
        book.setSynopsis("Romance naturalista brasileiro...");
        book.setPageCount(320);
        book.setAuthorId(2L);
        book.setCoverUrl("https://example.com/cortico.jpg");
        return book;
    }
}
