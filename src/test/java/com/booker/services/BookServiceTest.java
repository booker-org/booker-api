package com.booker.services;

import com.booker.entities.Author;
import com.booker.entities.Book;
import com.booker.entities.Genre;
import com.booker.repositories.AuthorRepository;
import com.booker.repositories.BookRepository;
import com.booker.repositories.GenreRepository;
import jakarta.persistence.EntityNotFoundException;

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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

  @Mock
  private BookRepository bookRepository;

  @Mock
  private AuthorRepository authorRepository;

  @Mock
  private GenreRepository genreRepository;

  @Mock
  private SupabaseStorageService storageService;

  @InjectMocks
  private BookService bookService;

  private Book testBook;

  private final Genre genre1 = new Genre("Ficção", null);
  private final Genre genre2 = new Genre("Clássico", null);

  @BeforeEach
  void setUp() {
    testBook = createBaseBook();
    testBook.setId(1L);
  }

  private Author createBaseAuthor() {
    Author author = new Author();
    author.setId(1L);
    author.setName("Machado de Assis");
    author.setBiography("Considerado um dos maiores escritores brasileiros...");
    return author;
  }

  private Book createBaseBook() {
    return createBaseBook("Dom Casmurro");
  }

  private Book createBaseBook(String title) {
    Book book = new Book();
    book.setTitle(title);
    book.setSynopsis("A obra narra a vida de Bento Santiago...");
    book.setPageCount(256);
    book.setAuthor(createBaseAuthor());
    book.setGenres(Set.of(genre1, genre2));
    book.setCoverUrl("https://example.com/dom-casmurro.jpg");
    return book;
  }

  // ========== FIND ==========

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

  @Test
  void findByTitle_ShouldReturnBooksMatchingTitle() {
    // Given
    Book book1 = createBaseBook("Dom Casmurro");
    book1.setId(1L);
    Book book2 = createBaseBook("Dom Pedro");
    book2.setId(2L);

    List<Book> books = Arrays.asList(book1, book2);
    Page<Book> bookPage = new PageImpl<>(books);
    Pageable pageable = PageRequest.of(0, 10);

    when(bookRepository.findByTitleContainingIgnoreCase("Dom", pageable)).thenReturn(bookPage);

    // When
    Page<Book> result = bookService.findByTitle("Dom", pageable);

    // Then
    assertEquals(2, result.getContent().size());
    assertTrue(result.getContent().stream().allMatch(book -> book.getTitle().contains("Dom")));
    verify(bookRepository).findByTitleContainingIgnoreCase("Dom", pageable);
  }

  @Test
  void findByAuthor_ShouldReturnBooksByAuthor() {
    // Given
    Long authorId = 1L;
    List<Book> books = Arrays.asList(testBook, createBaseBook("O Cortiço"));
    Page<Book> bookPage = new PageImpl<>(books);
    Pageable pageable = PageRequest.of(0, 10);

    when(bookRepository.findByAuthorId(authorId, pageable)).thenReturn(bookPage);

    // When
    Page<Book> result = bookService.findByAuthor(authorId, pageable);

    // Then
    assertEquals(2, result.getContent().size());
    verify(bookRepository).findByAuthorId(authorId, pageable);
  }

  @Test
  void findAll_ShouldReturnPageOfBooks() {
    // Given - Some books
    List<Book> books = Arrays.asList(testBook, createBaseBook("O Cortiço"));
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

  // ========== SAVE TESTS ==========

  @Test
  void save_ShouldReturnSavedBook_WhenValidBook() {
    // Given - Valid book
    Book newBook = createBaseBook();
    Book savedBook = createBaseBook();
    savedBook.setId(1L);

    Author mockAuthor = createBaseAuthor();
    when(authorRepository.findById(1L)).thenReturn(Optional.of(mockAuthor));

    Genre mockGenre1 = new Genre();
    mockGenre1.setId(1L);
    mockGenre1.setName("Ficção");

    when(genreRepository.findById(1L)).thenReturn(Optional.of(mockGenre1));
    when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

    // When
    Book result = bookService.save(newBook, 1L, List.of(1L));

    // Then
    assertNotNull(result.getId());
    assertEquals(newBook.getTitle(), result.getTitle());
    assertEquals(newBook.getSynopsis(), result.getSynopsis());
    verify(authorRepository).findById(1L);
    verify(genreRepository).findById(1L);
    verify(bookRepository).save(any(Book.class));
  }

  @Test
  void save_ShouldThrowException_WhenBookIsNull() {
    // Given - Null book

    // When & Then
    assertThrows(IllegalArgumentException.class, () -> {
      bookService.save(null, 1L, List.of(1L, 2L));
    });

    verify(bookRepository, never()).save(any());
  }

  @Test
  void save_ShouldThrowException_WhenTitleIsNull() {
    // Given
    Book invalidBook = createBaseBook();
    invalidBook.setTitle(null);

    // When & Then
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      bookService.save(invalidBook, 1L, List.of(1L));
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
      bookService.save(invalidBook, 1L, List.of(1L));
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
      bookService.save(invalidBook, 1L, List.of(1L));
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
      bookService.save(invalidBook, 1L, List.of(1L));
    });

    assertEquals("Número de páginas deve ser maior que zero", exception.getMessage());
    verify(bookRepository, never()).save(any());
  }

  @Test
  void save_ShouldThrowException_WhenAuthorNotFound() {
    // Given - Author not found
    Book validBook = createBaseBook();
    when(authorRepository.findById(999L)).thenReturn(Optional.empty());

    // When & Then
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      bookService.save(validBook, 999L, List.of(1L));
    });

    assertTrue(exception.getMessage().contains("ID do autor"));
    verify(authorRepository).findById(999L);
    verify(bookRepository, never()).save(any());
  }

  @Test
  void save_ShouldThrowException_WhenGenreNotFound() {
    // Given - Genre not found
    Book validBook = createBaseBook();

    Author mockAuthor = createBaseAuthor();
    when(authorRepository.findById(1L)).thenReturn(Optional.of(mockAuthor));
    when(genreRepository.findById(999L)).thenReturn(Optional.empty());

    // When & Then
    EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
      bookService.save(validBook, 1L, List.of(999L));
    });

    assertTrue(exception.getMessage().contains("Gênero não encontrado"));
    verify(genreRepository).findById(999L);
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

    Author mockAuthor = createBaseAuthor();
    when(authorRepository.findById(1L)).thenReturn(Optional.of(mockAuthor));

    Genre mockGenre1 = new Genre();
    mockGenre1.setId(1L);
    mockGenre1.setName("Ficção");

    when(genreRepository.findById(1L)).thenReturn(Optional.of(mockGenre1));

    when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
    when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);

    // When
    Optional<Book> result = bookService.update(bookId, updateRequest, 1L, List.of(1L));

    // Then
    assertTrue(result.isPresent());
    assertEquals("Título Atualizado", result.get().getTitle());
    assertEquals(300, result.get().getPageCount());
    verify(authorRepository).findById(1L);
    verify(genreRepository).findById(1L);
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
    Optional<Book> result = bookService.update(nonExistentId, updateRequest, 1L, List.of(1L, 2L));

    // Then
    assertFalse(result.isPresent());
    verify(bookRepository).findById(nonExistentId);
    verify(bookRepository, never()).save(any());
  }

  // ========== PARTIAL UPDATE TESTS ==========

  @Test
  void partialUpdate_ShouldUpdateOnlyProvidedFields() {
    // Given - Partial update
    Long bookId = 1L;
    Book partialUpdate = new Book();
    partialUpdate.setTitle("Título Parcialmente Atualizado");
    partialUpdate.setPageCount(400);

    Book existingBook = createBaseBook();
    existingBook.setId(bookId);
    existingBook.setTitle("Título Original");
    existingBook.setSynopsis("Synopsis Original");
    existingBook.setPageCount(200);
    existingBook.setCoverUrl("url-original.jpg");

    when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
    when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    Optional<Book> result = bookService.partialUpdate(bookId, partialUpdate, null, null);

    // Then
    assertTrue(result.isPresent());
    Book updatedBook = result.get();

    assertEquals("Título Parcialmente Atualizado", updatedBook.getTitle());
    assertEquals(400, updatedBook.getPageCount());

    assertEquals("Synopsis Original", updatedBook.getSynopsis());
    assertEquals("url-original.jpg", updatedBook.getCoverUrl());

    verify(bookRepository).findById(bookId);
    verify(bookRepository).save(any(Book.class));
  }

  @Test
  void partialUpdate_ShouldReturnEmpty_WhenBookNotExists() {
    // Given - Non-existent Book
    Long nonExistentId = 999L;
    Book partialUpdate = new Book();
    partialUpdate.setTitle("Título Qualquer");

    when(bookRepository.findById(nonExistentId)).thenReturn(Optional.empty());

    // When
    Optional<Book> result = bookService.partialUpdate(nonExistentId, partialUpdate, null, null);

    // Then
    assertFalse(result.isPresent());
    verify(bookRepository).findById(nonExistentId);
    verify(bookRepository, never()).save(any());
  }

  @Test
  void partialUpdate_ShouldValidateNonNullFields() {
    // Given - Partial update with invalid pageCount
    Long bookId = 1L;
    Book partialUpdate = new Book();
    partialUpdate.setPageCount(-10);

    Book existingBook = createBaseBook();
    existingBook.setId(bookId);

    when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));

    // When & Then
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      bookService.partialUpdate(bookId, partialUpdate, null, null);
    });

    assertEquals("Número de páginas deve ser maior que zero", exception.getMessage());
    verify(bookRepository, never()).save(any());
  }

  @Test
  void partialUpdate_ShouldUpdateAuthorAndGenres_WhenProvided() {
    // Given - Update with new author and genres
    Long bookId = 1L;
    Book partialUpdate = new Book();
    partialUpdate.setTitle("Novo Título");

    Book existingBook = createBaseBook();
    existingBook.setId(bookId);

    Author newAuthor = new Author();
    newAuthor.setId(2L);
    newAuthor.setName("Novo Autor");

    Genre newGenre = new Genre();
    newGenre.setId(3L);
    newGenre.setName("Novo Gênero");

    when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
    when(authorRepository.findById(2L)).thenReturn(Optional.of(newAuthor));
    when(genreRepository.findById(3L)).thenReturn(Optional.of(newGenre));
    when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    Optional<Book> result = bookService.partialUpdate(bookId, partialUpdate, 2L, List.of(3L));

    // Then
    assertTrue(result.isPresent());
    Book updatedBook = result.get();

    assertEquals("Novo Título", updatedBook.getTitle());
    assertEquals("Novo Autor", updatedBook.getAuthor().getName());
    assertEquals(1, updatedBook.getGenres().size());
    assertTrue(updatedBook.getGenres().stream().anyMatch(g -> "Novo Gênero".equals(g.getName())));

    verify(authorRepository).findById(2L);
    verify(genreRepository).findById(3L);
    verify(bookRepository).save(any(Book.class));
  }

  // ========== DELETE TESTS ==========

  @Test
  void deleteById_ShouldReturnTrue_WhenBookExists() throws Exception {
    // Given - Existing book with cover
    Book bookWithCover = createBaseBook();
    bookWithCover.setId(1L);
    bookWithCover.setCoverUrl("https://supabase.co/storage/v1/object/public/bucket/covers/test.jpg");

    when(bookRepository.findById(1L)).thenReturn(Optional.of(bookWithCover));
    when(storageService.extractFileNameFromUrl(anyString())).thenReturn("covers/test.jpg");

    // When
    boolean result = bookService.deleteById(1L);

    // Then
    assertTrue(result);
    verify(bookRepository).findById(1L);
    verify(storageService).extractFileNameFromUrl(bookWithCover.getCoverUrl());
    verify(storageService).deleteCover("covers/test.jpg");
    verify(bookRepository).deleteById(1L);
  }

  @Test
  void deleteById_ShouldReturnTrue_WhenBookExistsWithoutCover() throws Exception {
    // Given - Existing book without cover
    Book bookWithoutCover = createBaseBook();
    bookWithoutCover.setId(1L);
    bookWithoutCover.setCoverUrl(null);

    when(bookRepository.findById(1L)).thenReturn(Optional.of(bookWithoutCover));

    // When
    boolean result = bookService.deleteById(1L);

    // Then
    assertTrue(result);
    verify(bookRepository).findById(1L);
    verify(storageService, never()).extractFileNameFromUrl(any());
    verify(storageService, never()).deleteCover(any());
    verify(bookRepository).deleteById(1L);
  }

  @Test
  void deleteById_ShouldReturnFalse_WhenBookNotExists() {
    // Given - Non-existent ID
    when(bookRepository.findById(999L)).thenReturn(Optional.empty());

    // When
    boolean result = bookService.deleteById(999L);

    // Then
    assertFalse(result);
    verify(bookRepository).findById(999L);
    verify(bookRepository, never()).deleteById(any());
    verify(storageService, never()).deleteCover(any());
  }

  // ========== SEARCH TESTS ==========

  @Test
  void searchBooks_ShouldReturnMatchingBooks() {
    // Given
    String query = "romance";
    List<Book> books = Arrays.asList(testBook);
    Page<Book> bookPage = new PageImpl<>(books);
    Pageable pageable = PageRequest.of(0, 10);

    when(bookRepository.findByTitleOrSynopsisContaining(query, pageable)).thenReturn(bookPage);

    // When
    Page<Book> result = bookService.searchBooks(query, pageable);

    // Then
    assertEquals(1, result.getContent().size());
    verify(bookRepository).findByTitleOrSynopsisContaining(query, pageable);
  }

}
