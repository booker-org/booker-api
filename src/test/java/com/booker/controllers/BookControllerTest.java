package com.booker.controllers;

import com.booker.dtos.BookCreateDTO;
import com.booker.entities.Author;
import com.booker.entities.Book;
import com.booker.entities.Genre;
import com.booker.mappers.AuthorMapper;
import com.booker.mappers.BookMapper;
import com.booker.mappers.GenreMapper;
import com.booker.services.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookController.class, includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
    BookMapper.class, AuthorMapper.class, GenreMapper.class }))
class BookControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private BookService bookService;

  private final Genre genre1 = new Genre("Ficção", null);
  private final Genre genre2 = new Genre("Clássico", null);

  private Author createBaseAuthor() {
    Author author = new Author();
    author.setName("Machado de Assis");
    author.setBiography("Considerado um dos maiores escritores brasileiros...");
    return author;
  }

  private Book createBaseBook() {
    Book book = new Book();
    book.setTitle("Dom Casmurro");
    book.setSynopsis("A obra narra a vida de Bento Santiago...");
    book.setPageCount(256);
    book.setAuthor(createBaseAuthor());
    book.setGenres(Set.of(genre1, genre2));
    book.setCoverUrl("https://example.com/dom-casmurro.jpg");
    return book;
  }

  // SUCCESS CASES

  // ========== GET TESTS ==========

  @Test
  void getBookById_ShouldReturnBook_WhenBookExists() throws Exception {
    // Given - Preparar dados de teste
    Long bookId = 1L;
    Book bookMock = createBaseBook();
    bookMock.setId(bookId);

    // When - Configurar comportamento do mock
    when(bookService.findById(bookId)).thenReturn(Optional.of(bookMock));

    // Then - Executar e verificar
    mockMvc.perform(get("/books/{id}", bookId))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(bookId))
        .andExpect(jsonPath("$.title").value("Dom Casmurro"))
        .andExpect(jsonPath("$.synopsis").value("A obra narra a vida de Bento Santiago..."))
        .andExpect(jsonPath("$.pageCount").value(256))
        .andExpect(jsonPath("$.author.name").value("Machado de Assis"))
        .andExpect(jsonPath("$.genres").isArray())
        .andExpect(jsonPath("$.coverUrl").value("https://example.com/dom-casmurro.jpg"));
  }

  // ========== POST TESTS ==========

  @Test
  void createBook_ShouldReturnCreatedBook_WhenValidRequest() throws Exception {
    // Given
    BookCreateDTO newBookRequest = new BookCreateDTO(
        "Dom Casmurro",
        "A obra narra a vida de Bento Santiago...",
        256,
        1L, // authorId
        List.of(1L, 2L)); // genreIds

    Book savedBook = createBaseBook();
    savedBook.setId(1L);

    // When
    when(bookService.save(any(Book.class), eq(1L), eq(List.of(1L, 2L)), eq(null))).thenReturn(savedBook);

    // Create MockMultipartFile for the book data as JSON
    MockMultipartFile bookPart = new MockMultipartFile(
        "book",
        "",
        "application/json",
        objectMapper.writeValueAsString(newBookRequest).getBytes());

    // Then
    mockMvc.perform(multipart("/books")
        .file(bookPart))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.title").value("Dom Casmurro"))
        .andExpect(jsonPath("$.pageCount").value(256));
  }

  @Test
  void createBook_ShouldReturnCreatedBook_WhenValidRequestWithCover() throws Exception {
    // Given
    BookCreateDTO newBookRequest = new BookCreateDTO(
        "Dom Casmurro",
        "A obra narra a vida de Bento Santiago...",
        256,
        1L, // authorId
        List.of(1L, 2L)); // genreIds

    Book savedBook = createBaseBook();
    savedBook.setId(1L);

    // Create mock cover file
    MockMultipartFile coverFile = new MockMultipartFile(
        "cover",
        "cover.jpg",
        "image/jpeg",
        "fake image content".getBytes());

    // When
    when(bookService.save(any(Book.class), eq(1L), eq(List.of(1L, 2L)), any())).thenReturn(savedBook);

    // Create MockMultipartFile for the book data as JSON
    MockMultipartFile bookPart = new MockMultipartFile(
        "book",
        "",
        "application/json",
        objectMapper.writeValueAsString(newBookRequest).getBytes());

    // Then
    mockMvc.perform(multipart("/books")
        .file(bookPart)
        .file(coverFile))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.title").value("Dom Casmurro"))
        .andExpect(jsonPath("$.pageCount").value(256));
  }

  // ========== PUT TESTS ==========

  @Test
  void updateBook_ShouldReturnUpdatedBook_WhenValidRequest() throws Exception {
    // Given - Existing book ID and update data using DTO
    Long bookId = 1L;
    BookCreateDTO updateRequest = new BookCreateDTO(
        "Dom Casmurro - Updated",
        "Updated synopsis...",
        300,
        1L,
        List.of(1L, 2L));

    Book expectedResult = createBaseBook();
    expectedResult.setId(bookId);
    expectedResult.setTitle("Dom Casmurro - Updated");
    expectedResult.setSynopsis("Updated synopsis...");
    expectedResult.setPageCount(300);
    expectedResult.setCoverUrl("https://example.com/dom-casmurro-updated.jpg");

    // When
    when(bookService.update(eq(bookId), any(Book.class), eq(1L), eq(List.of(1L, 2L))))
        .thenReturn(Optional.of(expectedResult));
    when(bookService.update(eq(bookId), any(Book.class), eq(1L), eq(List.of(1L, 2L)), eq(null)))
        .thenReturn(Optional.of(expectedResult));

    // Then
    MockMultipartFile bookPart = new MockMultipartFile(
        "book",
        "book.json",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(updateRequest));

    mockMvc.perform(multipart("/books/{id}", bookId)
        .file(bookPart)
        .with(request -> {
          request.setMethod("PUT");
          return request;
        }))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(bookId))
        .andExpect(jsonPath("$.title").value("Dom Casmurro - Updated"))
        .andExpect(jsonPath("$.pageCount").value(300));
  }

  // ========== PATCH TESTS ==========

  @Test
  void patchBook_ShouldUpdateFields_WhenValidRequest() throws Exception {
    // Given - Existing book with original data
    Long bookId = 1L;
    BookCreateDTO patchRequest = new BookCreateDTO(
        "Novo Título",
        null,
        null,
        1L,
        List.of(1L, 2L));

    Book expectedResult = createBaseBook();
    expectedResult.setId(bookId);
    expectedResult.setTitle("Novo Título");

    // When - Mock behavior
    when(bookService.partialUpdate(eq(bookId), any(Book.class), eq(1L), eq(List.of(1L, 2L))))
        .thenReturn(Optional.of(expectedResult));
    when(bookService.partialUpdate(eq(bookId), any(Book.class), eq(1L), eq(List.of(1L, 2L)), eq(null)))
        .thenReturn(Optional.of(expectedResult));

    // Then
    MockMultipartFile bookPart = new MockMultipartFile(
        "book",
        "book.json",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(patchRequest));

    mockMvc.perform(multipart("/books/{id}", bookId)
        .file(bookPart)
        .with(request -> {
          request.setMethod("PATCH");
          return request;
        }))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(bookId))
        .andExpect(jsonPath("$.title").value("Novo Título"))
        .andExpect(jsonPath("$.synopsis").value("A obra narra a vida de Bento Santiago..."))
        .andExpect(jsonPath("$.pageCount").value(256))
        .andExpect(jsonPath("$.coverUrl").value("https://example.com/dom-casmurro.jpg"));
  }

  // ========== DELETE TESTS ==========

  @Test
  void deleteBook_ShouldReturnNoContent_WhenBookExists() throws Exception {
    // Given - Existing book ID
    Long bookId = 1L;

    // When - Mock confirma que o livro foi deletado
    when(bookService.deleteById(bookId)).thenReturn(true);

    // Then
    mockMvc.perform(delete("/books/{id}", bookId))
        .andExpect(status().isNoContent());
  }

  // ========== SEARCH TESTS ==========

  @Test
  void getBooksByTitle_ShouldReturnMatchingBooks() throws Exception {
    // Given
    Book book1 = createBaseBook();
    book1.setId(1L);
    book1.setTitle("Dom Casmurro");

    Book book2 = createBaseBook();
    book2.setId(2L);
    book2.setTitle("Dom Pedro");

    List<Book> books = Arrays.asList(book1, book2);
    Page<Book> bookPage = new PageImpl<>(books);

    when(bookService.findByTitle(eq("Dom"), any(Pageable.class))).thenReturn(bookPage);

    // When & Then
    mockMvc.perform(get("/books")
        .param("title", "Dom")
        .param("page", "0")
        .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.content[0].title").value("Dom Casmurro"))
        .andExpect(jsonPath("$.content[1].title").value("Dom Pedro"));
  }

  @Test
  void getBooksByAuthor_ShouldReturnBooksByAuthor() throws Exception {
    // Given
    Long authorId = 1L;
    List<Book> books = Arrays.asList(createBaseBook());
    Page<Book> bookPage = new PageImpl<>(books);

    when(bookService.findByAuthor(eq(authorId), any(Pageable.class))).thenReturn(bookPage);

    // When & Then
    mockMvc.perform(get("/books")
        .param("authorId", "1")
        .param("page", "0")
        .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(1));
  }

  @Test
  void getAllBooks_ShouldReturnPageOfBooks() throws Exception {
    // Given
    List<Book> books = Arrays.asList(createBaseBook(), createBaseBook());
    Page<Book> bookPage = new PageImpl<>(books);

    when(bookService.findAll(any(Pageable.class))).thenReturn(bookPage);

    // When & Then
    mockMvc.perform(get("/books")
        .param("page", "0")
        .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(2));
  }

  // FAILURE CASES - VALIDATION

  // ========== GET TESTS ==========

  @Test
  void getBookById_ShouldReturn404_WhenBookNotExists() throws Exception {
    // Given
    Long nonExistentBookId = 999L;

    // When
    when(bookService.findById(nonExistentBookId)).thenReturn(Optional.empty());

    // Then
    mockMvc.perform(get("/books/{id}", nonExistentBookId))
        .andExpect(status().isNotFound());
  }

  // ========== POST TESTS ==========

  @Test
  void createBook_ShouldReturn400_WhenServiceThrowsException() throws Exception {
    // Given - Invalid request using DTO
    BookCreateDTO invalidBookRequest = new BookCreateDTO(
        null, // título inválido
        "A obra narra a vida de Bento Santiago...",
        256,
        1L, // authorId
        List.of(1L, 2L)); // genreIds

    // When
    when(bookService.save(any(Book.class), eq(1L), eq(List.of(1L, 2L))))
        .thenThrow(new IllegalArgumentException("Dados do livro inválidos"));
    when(bookService.save(any(Book.class), eq(1L), eq(List.of(1L, 2L)), eq(null)))
        .thenThrow(new IllegalArgumentException("Dados do livro inválidos"));

    // Then
    mockMvc.perform(post("/books")
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .param("book", objectMapper.writeValueAsString(invalidBookRequest)))
        .andExpect(status().isBadRequest());
  }

  // ========== PUT TESTS ==========

  @Test
  void updateBook_ShouldReturn404_WhenBookNotExists() throws Exception {
    // Given - Non-existing book ID using DTO
    Long nonExistentBookId = 999L;
    BookCreateDTO updateRequest = new BookCreateDTO(
        "Updated Title",
        "Updated synopsis",
        256,
        1L,
        List.of(1L, 2L));

    // When
    when(bookService.update(eq(nonExistentBookId), any(Book.class), eq(1L), eq(List.of(1L, 2L))))
        .thenReturn(Optional.empty());
    when(bookService.update(eq(nonExistentBookId), any(Book.class), eq(1L), eq(List.of(1L, 2L)), eq(null)))
        .thenReturn(Optional.empty());

    // Then
    MockMultipartFile bookPart = new MockMultipartFile(
        "book",
        "book.json",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(updateRequest));

    mockMvc.perform(multipart("/books/{id}", nonExistentBookId)
        .file(bookPart)
        .with(request -> {
          request.setMethod("PUT");
          return request;
        }))
        .andExpect(status().isNotFound());
  }

  @Test
  void updateBook_ShouldReturn400_WhenServiceThrowsException() throws Exception {
    // Given - Existing book ID with invalid update data using DTO
    Long bookId = 1L;
    BookCreateDTO invalidUpdateRequest = new BookCreateDTO(
        "",
        "A obra narra a vida de Bento Santiago...",
        256,
        1L,
        List.of(1L, 2L));

    // When
    when(bookService.update(eq(bookId), any(Book.class), eq(1L), eq(List.of(1L, 2L))))
        .thenThrow(new IllegalArgumentException("Título deve ter entre 2 e 100 caracteres"));
    when(bookService.update(eq(bookId), any(Book.class), eq(1L), eq(List.of(1L, 2L)), eq(null)))
        .thenThrow(new IllegalArgumentException("Título deve ter entre 2 e 100 caracteres"));

    // Then
    MockMultipartFile bookPart = new MockMultipartFile(
        "book",
        "book.json",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(invalidUpdateRequest));

    mockMvc.perform(multipart("/books/{id}", bookId)
        .file(bookPart)
        .with(request -> {
          request.setMethod("PUT");
          return request;
        }))
        .andExpect(status().isBadRequest());
  }

  // ========== PATCH TESTS ==========

  @Test
  void patchBook_ShouldReturn404_WhenBookNotExists() throws Exception {
    // Given - Non-existing book ID using DTO
    Long nonExistentBookId = 999L;
    BookCreateDTO patchRequest = new BookCreateDTO(
        "Updated Title",
        null,
        null,
        1L,
        List.of(1L, 2L));

    // When
    when(bookService.partialUpdate(eq(nonExistentBookId), any(Book.class), eq(1L), eq(List.of(1L, 2L))))
        .thenReturn(Optional.empty());
    when(bookService.partialUpdate(eq(nonExistentBookId), any(Book.class), eq(1L), eq(List.of(1L, 2L)), eq(null)))
        .thenReturn(Optional.empty());

    // Then
    mockMvc.perform(patch("/books/{id}", nonExistentBookId)
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .param("book", objectMapper.writeValueAsString(patchRequest)))
        .andExpect(status().isNotFound());
  }

  // ========== DELETE TESTS ==========

  @Test
  void deleteBook_ShouldReturn404_WhenBookNotExists() throws Exception {
    // Given - Non-existing book ID
    Long nonExistentBookId = 999L;

    // When
    when(bookService.deleteById(nonExistentBookId)).thenReturn(false);

    // Then
    mockMvc.perform(delete("/books/{id}", nonExistentBookId))
        .andExpect(status().isNotFound());
  }

}
