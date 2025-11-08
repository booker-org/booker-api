package com.booker.controllers;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.booker.DTO.Author.BookCreateDTO;
import com.booker.mappers.AuthorMapper;
import com.booker.mappers.BookMapper;
import com.booker.mappers.GenreMapper;
import com.booker.models.Author;
import com.booker.models.Book;
import com.booker.models.Genre;
import com.booker.services.BookService;

@WebMvcTest(
  controllers = BookController.class,
  includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
    BookMapper.class,
    AuthorMapper.class,
    GenreMapper.class
  })
)
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

  @Test
  void getBookById_ShouldReturnBook_WhenBookExists() throws Exception {
    Long bookId = 1L;
    Book bookMock = createBaseBook();

    bookMock.setId(bookId);

    when(bookService.findById(bookId)).thenReturn(Optional.of(bookMock));

    mockMvc.perform(get("/books/{id}", bookId))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(bookId))
      .andExpect(jsonPath("$.title").value("Dom Casmurro"))
      .andExpect(jsonPath("$.coverUrl").value("https://example.com/dom-casmurro.jpg"))
    ;
  }

  @Test
  void createBook_ShouldReturnCreatedBook_WhenValidRequest() throws Exception {
    BookCreateDTO request = new BookCreateDTO(
      "Dom Casmurro",
      "A obra narra a vida de Bento Santiago...",
      256,
      1L,
      List.of(1L, 2L)
    );

    Book savedBook = createBaseBook();
    savedBook.setId(1L);

    when(bookService.save(any(Book.class), eq(1L), eq(List.of(1L, 2L)))).thenReturn(savedBook);

    mockMvc.perform(post("/books")
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(1))
      .andExpect(jsonPath("$.title").value("Dom Casmurro"))
    ;
  }

  @Test
  void createBook_ShouldReturn400_WhenServiceThrows() throws Exception {
    BookCreateDTO request = new BookCreateDTO(
      null,
      "Sinopse",
      256,
      1L,
      List.of(1L, 2L)
    );

    when(bookService.save(any(Book.class), eq(1L), eq(List.of(1L, 2L))))
      .thenThrow(new IllegalArgumentException("Dados inválidos"))
    ;

    mockMvc.perform(post("/books")
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest())
    ;
  }

  @Test
  void updateBook_ShouldReturnUpdatedBook_WhenValidRequest() throws Exception {
    Long bookId = 1L;

    BookCreateDTO request = new BookCreateDTO(
      "Dom Casmurro - Updated",
      "Updated synopsis...",
      300,
      1L,
      List.of(1L, 2L)
    );

    Book updated = createBaseBook();
    updated.setId(bookId);
    updated.setTitle("Dom Casmurro - Updated");
    updated.setSynopsis("Updated synopsis...");
    updated.setPageCount(300);

    when(bookService.update(eq(bookId), any(Book.class), eq(1L), eq(List.of(1L, 2L))))
      .thenReturn(Optional.of(updated))
    ;

    mockMvc.perform(put("/books/{id}", bookId)
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.title").value("Dom Casmurro - Updated"))
      .andExpect(jsonPath("$.pageCount").value(300))
    ;
  }

  @Test
  void updateBook_ShouldReturn404_WhenNotFound() throws Exception {
    Long bookId = 99L;

    BookCreateDTO request = new BookCreateDTO(
      "Title",
      "Sinopse",
      200,
      1L,
      List.of(1L)
    );

    when(bookService.update(eq(bookId), any(Book.class), eq(1L), eq(List.of(1L))))
      .thenReturn(Optional.empty())
    ;

    mockMvc.perform(put("/books/{id}", bookId)
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isNotFound())
    ;
  }

  @Test
  void patchBook_ShouldReturnUpdatedBook_WhenValidRequest() throws Exception {
    Long bookId = 1L;

    BookCreateDTO request = new BookCreateDTO(
      "Novo Título",
      null,
      null,
      null,
      null
    );

    Book patched = createBaseBook();
    patched.setId(bookId);
    patched.setTitle("Novo Título");

    when(bookService.partialUpdate(eq(bookId), any(Book.class), isNull(), isNull()))
      .thenReturn(Optional.of(patched))
    ;

    mockMvc.perform(patch("/books/{id}", bookId)
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.title").value("Novo Título"))
    ;
  }

  @Test
  void patchBook_ShouldReturn404_WhenNotFound() throws Exception {
    Long bookId = 99L;

    BookCreateDTO request = new BookCreateDTO(
      "Novo",
      null,
      null,
      null,
      null
    );

    when(bookService.partialUpdate(eq(bookId), any(Book.class), isNull(), isNull()))
      .thenReturn(Optional.empty())
    ;

    mockMvc.perform(patch("/books/{id}", bookId)
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isNotFound())
    ;
  }

  @Test
  void uploadCover_ShouldReturnUpdatedBook() throws Exception {
    Long bookId = 1L;
    Book updated = createBaseBook();

    updated.setId(bookId);
    updated.setCoverUrl("https://example.com/new-cover.jpg");

    MockMultipartFile cover = new MockMultipartFile(
      "cover",
      "cover.jpg",
      "image/jpeg",
      "fake".getBytes()
    );

    when(bookService.updateCover(eq(bookId), any())).thenReturn(Optional.of(updated));

    mockMvc.perform(multipart("/books/{id}/cover", bookId)
      .file(cover)
      .with(request -> {
        request.setMethod("PUT");
        return request;
      }))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.coverUrl").value("https://example.com/new-cover.jpg")
    );
  }

  @Test
  void uploadCover_ShouldReturn404_WhenBookNotFound() throws Exception {
    Long bookId = 99L;

    MockMultipartFile cover = new MockMultipartFile(
      "cover",
      "cover.jpg",
      "image/jpeg",
      "fake".getBytes()
    );

    when(bookService.updateCover(eq(bookId), any())).thenReturn(Optional.empty());

    mockMvc.perform(multipart("/books/{id}/cover", bookId)
      .file(cover)
      .with(request -> {
        request.setMethod("PUT");
        return request;
      }))
      .andExpect(status().isNotFound()
    );
  }

  @Test
  void deleteCover_ShouldReturnNoContent() throws Exception {
    Long bookId = 1L;
    when(bookService.removeCover(bookId)).thenReturn(Optional.of(createBaseBook()));

    mockMvc.perform(delete("/books/{id}/cover", bookId))
      .andExpect(status().isNoContent())
    ;
  }

  @Test
  void deleteCover_ShouldReturn404_WhenBookNotFound() throws Exception {
    Long bookId = 99L;
    when(bookService.removeCover(bookId)).thenReturn(Optional.empty());

    mockMvc.perform(delete("/books/{id}/cover", bookId))
      .andExpect(status().isNotFound())
    ;
  }

  @Test
  void deleteBook_ShouldReturnNoContent_WhenDeleted() throws Exception {
    Long bookId = 1L;
    when(bookService.deleteById(bookId)).thenReturn(true);

    mockMvc.perform(delete("/books/{id}", bookId))
      .andExpect(status().isNoContent())
    ;
  }

  @Test
  void deleteBook_ShouldReturn404_WhenNotFound() throws Exception {
    Long bookId = 99L;
    when(bookService.deleteById(bookId)).thenReturn(false);

    mockMvc.perform(delete("/books/{id}", bookId))
      .andExpect(status().isNotFound())
    ;
  }

  @Test
  void searchByTitle_ShouldReturnPagedResult() throws Exception {
    Page<Book> page = new PageImpl<>(List.of(createBaseBook()));

    when(bookService.findByTitle(eq("Dom"), any(Pageable.class))).thenReturn(page);

    mockMvc.perform(get("/books").param("title", "Dom"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.content[0].title").value("Dom Casmurro"))
    ;
  }

  @Test
  void getAllBooks_ShouldReturnPagedResult() throws Exception {
    Page<Book> page = new PageImpl<>(List.of(createBaseBook()));

    when(bookService.findAll(any(Pageable.class))).thenReturn(page);

    mockMvc.perform(get("/books"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.content").isArray())
    ;
  }
}