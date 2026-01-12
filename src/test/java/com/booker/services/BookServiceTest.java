package com.booker.services;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.booker.models.Author;
import com.booker.models.Book;
import com.booker.models.Genre;
import com.booker.repositories.BookRepository;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
  @Mock
  private BookRepository bookRepository;

  @Mock
  private AuthorService authorService;

  @Mock
  private GenreService genreService;

  @Mock
  private SupabaseStorageService storageService;

  @InjectMocks
  private BookService bookService;

  private Book testBook;

  private final Genre genre1 = new Genre("Ficção", null);
  private final Genre genre2 = new Genre("Clássico", null);

  private final UUID TEST_BOOK_ID = UUID.randomUUID();
  private final UUID TEST_AUTHOR_ID = UUID.randomUUID();

  @BeforeEach
  void setUp() {
    testBook = createBaseBook();
    testBook.setId(TEST_BOOK_ID);
  }

  private Author createBaseAuthor() {
    Author author = new Author();

    author.setId(TEST_AUTHOR_ID);
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
    when(bookRepository.findById(TEST_BOOK_ID)).thenReturn(Optional.of(testBook));

    // When
    Optional<Book> result = bookService.findById(TEST_BOOK_ID);

    // Then
    assertTrue(result.isPresent());
    assertEquals(testBook.getId(), result.get().getId());
    assertEquals(testBook.getTitle(), result.get().getTitle());
    verify(bookRepository).findById(TEST_BOOK_ID);
  }

  @Test
  void findById_ShouldReturnEmpty_WhenBookNotExists() {
    final UUID randomID = UUID.randomUUID();

    // Given - Non-existent ID
    when(bookRepository.findById(randomID)).thenReturn(Optional.empty());

    // When
    Optional<Book> result = bookService.findById(randomID);

    // Then
    assertFalse(result.isPresent());
    verify(bookRepository).findById(randomID);
  }

  @Test
  void findByTitle_ShouldReturnBooksMatchingTitle() {
    // Given
    Book book1 = createBaseBook("Dom Casmurro");
    book1.setId(UUID.randomUUID());

    Book book2 = createBaseBook("Dom Pedro");
    book2.setId(UUID.randomUUID());

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
    final UUID authorId = UUID.randomUUID();

    // Given
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
    // Given - IDs para autor e gênero
    final UUID authorId = UUID.randomUUID();
    final UUID genreId = UUID.randomUUID();

    // Livro novo (sem ID ainda) e livro salvo (com ID gerado)
    Book newBook = createBaseBook();

    Book savedBook = createBaseBook();
    savedBook.setId(UUID.randomUUID());

    // Mock do autor
    Author mockAuthor = createBaseAuthor();
    mockAuthor.setId(authorId);
    when(authorService.findById(authorId)).thenReturn(Optional.of(mockAuthor));

    // Mock do gênero
    Genre mockGenre1 = new Genre();
    mockGenre1.setId(genreId);
    mockGenre1.setName("Ficção");
    when(genreService.findById(genreId)).thenReturn(Optional.of(mockGenre1));

    // Mock do save
    when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

    // When
    Book result = bookService.save(newBook, authorId, List.of(genreId));

    // Then
    assertNotNull(result.getId());
    assertEquals(newBook.getTitle(), result.getTitle());
    assertEquals(newBook.getSynopsis(), result.getSynopsis());
    verify(authorService).findById(authorId);
    verify(genreService).findById(genreId);
    verify(bookRepository).save(any(Book.class));
  }

  @Test
  void save_ShouldThrowException_WhenBookIsNull() {
    // Given - IDs válidos mas livro nulo
    final UUID authorId = UUID.randomUUID();
    final UUID genreId = UUID.randomUUID();

    // When & Then
    assertThrows(IllegalArgumentException.class, () -> {
      bookService.save(null, authorId, List.of(genreId));
    });

    verify(bookRepository, never()).save(any());
  }

  @Test
  void save_ShouldThrowException_WhenTitleIsNull() {
    // Given
    final UUID authorId = UUID.randomUUID();
    final UUID genreId = UUID.randomUUID();

    Book invalidBook = createBaseBook();
    invalidBook.setTitle(null);

    // When & Then
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      bookService.save(invalidBook, authorId, List.of(genreId));
    });

    assertEquals("Título é obrigatório", exception.getMessage());
    verify(bookRepository, never()).save(any());
  }

  @Test
  void save_ShouldThrowException_WhenTitleIsTooShort() {
    // Given - Title too short
    final UUID authorId = UUID.randomUUID();
    final UUID genreId = UUID.randomUUID();

    Book invalidBook = createBaseBook();
    invalidBook.setTitle("A");

    // When & Then
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      bookService.save(invalidBook, authorId, List.of(genreId));
    });

    assertEquals("Título deve ter entre 2 e 200 caracteres", exception.getMessage());
    verify(bookRepository, never()).save(any());
  }

  @Test
  void save_ShouldThrowException_WhenTitleIsTooLong() {
    // Given - Title exceeding max length
    final UUID authorId = UUID.randomUUID();
    final UUID genreId = UUID.randomUUID();

    Book invalidBook = createBaseBook();
    invalidBook.setTitle("A".repeat(201));

    // When & Then
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      bookService.save(invalidBook, authorId, List.of(genreId));
    });

    assertEquals("Título deve ter entre 2 e 200 caracteres", exception.getMessage());
    verify(bookRepository, never()).save(any());
  }

  @Test
  void save_ShouldThrowException_WhenPageCountIsNegative() {
    // Given - Negative page count
    final UUID authorId = UUID.randomUUID();
    final UUID genreId = UUID.randomUUID();

    Book invalidBook = createBaseBook();
    invalidBook.setPageCount(-10);

    // When & Then
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      bookService.save(invalidBook, authorId, List.of(genreId));
    });

    assertEquals("Número de páginas deve ser maior que zero", exception.getMessage());
    verify(bookRepository, never()).save(any());
  }

  @Test
  void save_ShouldThrowException_WhenAuthorNotFound() {
    // Given - Author not found
    final UUID nonExistentAuthorId = UUID.randomUUID();
    final UUID genreId = UUID.randomUUID();

    Book validBook = createBaseBook();
    when(authorService.findById(nonExistentAuthorId)).thenReturn(Optional.empty());

    // When & Then
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      bookService.save(validBook, nonExistentAuthorId, List.of(genreId));
    });

    assertTrue(exception.getMessage().contains("ID do autor"));
    verify(authorService).findById(nonExistentAuthorId);
    verify(bookRepository, never()).save(any());
  }

  @Test
  void save_ShouldThrowException_WhenGenreNotFound() {
    // Given - Genre not found
    final UUID authorId = UUID.randomUUID();
    final UUID nonExistentGenreId = UUID.randomUUID();

    Book validBook = createBaseBook();

    Author mockAuthor = createBaseAuthor();
    mockAuthor.setId(authorId);
    when(authorService.findById(authorId)).thenReturn(Optional.of(mockAuthor));
    when(genreService.findById(nonExistentGenreId)).thenReturn(Optional.empty());

    // When & Then
    EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
      bookService.save(validBook, authorId, List.of(nonExistentGenreId));
    });

    assertTrue(exception.getMessage().contains("Gênero não encontrado"));
    verify(genreService).findById(nonExistentGenreId);
    verify(bookRepository, never()).save(any());
  }

  // ========== UPDATE TESTS ==========

  @Test
  void update_ShouldReturnUpdatedBook_WhenBookExists() {
    // Given - IDs válidos
    UUID bookId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    UUID genreId = UUID.randomUUID();

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
    mockAuthor.setId(authorId);
    when(authorService.findById(authorId)).thenReturn(Optional.of(mockAuthor));

    Genre mockGenre1 = new Genre();
    mockGenre1.setId(genreId);
    mockGenre1.setName("Ficção");
    when(genreService.findById(genreId)).thenReturn(Optional.of(mockGenre1));

    when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
    when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);

    // When
    Optional<Book> result = bookService.update(bookId, updateRequest, authorId, List.of(genreId));

    // Then
    assertTrue(result.isPresent());
    assertEquals("Título Atualizado", result.get().getTitle());
    assertEquals(300, result.get().getPageCount());
    verify(authorService).findById(authorId);
    verify(genreService).findById(genreId);
    verify(bookRepository).findById(bookId);
    verify(bookRepository).save(any(Book.class));
  }

  @Test
  void update_ShouldReturnEmpty_WhenBookNotExists() {
    // Given - ID não existente
    UUID nonExistentId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    UUID genreId = UUID.randomUUID();

    Book updateRequest = createBaseBook();

    when(bookRepository.findById(nonExistentId)).thenReturn(Optional.empty());

    // When
    Optional<Book> result = bookService.update(nonExistentId, updateRequest, authorId, List.of(genreId));

    // Then
    assertFalse(result.isPresent());
    verify(bookRepository).findById(nonExistentId);
    verify(bookRepository, never()).save(any());
  }

  // ========== PARTIAL UPDATE TESTS ==========

  @Test
  void partialUpdate_ShouldUpdateOnlyProvidedFields() {
    // Given - Partial update
    UUID bookId = UUID.randomUUID();
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
    UUID nonExistentId = UUID.randomUUID();
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
    UUID bookId = UUID.randomUUID();
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
    UUID bookId = UUID.randomUUID();
    UUID newAuthorId = UUID.randomUUID();
    UUID newGenreId = UUID.randomUUID();

    Book partialUpdate = new Book();
    partialUpdate.setTitle("Novo Título");

    Book existingBook = createBaseBook();
    existingBook.setId(bookId);

    Author newAuthor = new Author();
    newAuthor.setId(newAuthorId);
    newAuthor.setName("Novo Autor");

    Genre newGenre = new Genre();
    newGenre.setId(newGenreId);
    newGenre.setName("Novo Gênero");

    when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
    when(authorService.findById(newAuthorId)).thenReturn(Optional.of(newAuthor));
    when(genreService.findById(newGenreId)).thenReturn(Optional.of(newGenre));
    when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    Optional<Book> result = bookService.partialUpdate(bookId, partialUpdate, newAuthorId, List.of(newGenreId));

    // Then
    assertTrue(result.isPresent());
    Book updatedBook = result.get();

    assertEquals("Novo Título", updatedBook.getTitle());
    assertEquals("Novo Autor", updatedBook.getAuthor().getName());
    assertEquals(1, updatedBook.getGenres().size());
    assertTrue(updatedBook.getGenres().stream().anyMatch(g -> "Novo Gênero".equals(g.getName())));

    verify(authorService).findById(newAuthorId);
    verify(genreService).findById(newGenreId);
    verify(bookRepository).save(any(Book.class));
  }

  // ========== DELETE TESTS ==========

  @Test
  void deleteById_ShouldReturnTrue_WhenBookExists() throws Exception {
    // Given - Existing book with cover
    UUID bookId = UUID.randomUUID();
    Book bookWithCover = createBaseBook();
    bookWithCover.setId(bookId);
    bookWithCover.setCoverUrl("https://supabase.co/storage/v1/object/public/bucket/covers/test.jpg");

    when(bookRepository.findById(bookId)).thenReturn(Optional.of(bookWithCover));
    when(storageService.extractFileNameFromUrl(anyString())).thenReturn("covers/test.jpg");

    // When
    boolean result = bookService.deleteById(bookId);

    // Then
    assertTrue(result);
    verify(bookRepository).findById(bookId);
    verify(storageService).extractFileNameFromUrl(bookWithCover.getCoverUrl());
    verify(storageService).deleteCover("covers/test.jpg");
    verify(bookRepository).deleteById(bookId);
  }

  @Test
  void deleteById_ShouldReturnTrue_WhenBookExistsWithoutCover() throws Exception {
    // Given - Existing book without cover
    UUID bookId = UUID.randomUUID();
    Book bookWithoutCover = createBaseBook();
    bookWithoutCover.setId(bookId);
    bookWithoutCover.setCoverUrl(null);

    when(bookRepository.findById(bookId)).thenReturn(Optional.of(bookWithoutCover));

    // When
    boolean result = bookService.deleteById(bookId);

    // Then
    assertTrue(result);
    verify(bookRepository).findById(bookId);
    verify(storageService, never()).extractFileNameFromUrl(any());
    verify(storageService, never()).deleteCover(any());
    verify(bookRepository).deleteById(bookId);
  }

  @Test
  void deleteById_ShouldReturnFalse_WhenBookNotExists() {
    // Given - Non-existent ID
    UUID nonExistentId = UUID.randomUUID();
    when(bookRepository.findById(nonExistentId)).thenReturn(Optional.empty());

    // When
    boolean result = bookService.deleteById(nonExistentId);

    // Then
    assertFalse(result);
    verify(bookRepository).findById(nonExistentId);
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