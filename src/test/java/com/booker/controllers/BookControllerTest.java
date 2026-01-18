package com.booker.controllers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import com.booker.config.security.JwtAuthenticationFilter;
import com.booker.config.security.SecurityConfig;
import com.booker.DTO.Book.BookCreateDTO;
import com.booker.DTO.Book.BookDTO;
import com.booker.DTO.Book.BookDetailDTO;
import com.booker.mappers.AuthorMapper;
import com.booker.mappers.BookMapper;
import com.booker.mappers.GenreMapper;
import com.booker.models.Book;
import com.booker.services.BookService;
import com.booker.services.JwtService;

@WebMvcTest(
  controllers = BookController.class,
  includeFilters = @ComponentScan.Filter(
    type = FilterType.ASSIGNABLE_TYPE,
    classes = {
      BookMapper.class,
      AuthorMapper.class,
      GenreMapper.class
    }
  )
)
@Import({ SecurityConfig.class, JwtAuthenticationFilter.class })
@ActiveProfiles("test")
class BookControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private JwtService jwtService;

  @MockitoBean
  private UserDetailsService userDetailsService;

  @MockitoBean
  private PasswordEncoder passwordEncoder;

  private static final ObjectMapper objectMapper = JsonMapper.builder()
    .findAndAddModules()
    .build()
  ;

  @MockitoBean
  private BookService bookService;

  @Test
  void getBookById_ShouldReturnBook_WhenBookExists() throws Exception {
    final UUID bookId = UUID.randomUUID();

    BookDetailDTO bookDTO = new BookDetailDTO(
      bookId,
      "Dom Casmurro",
      "A obra narra a vida de Bento Santiago...",
      256,
      null, null,
      "https://example.com/dom-casmurro.jpg",
      null, null
    );

    when(bookService.findById(bookId)).thenReturn(bookDTO);

    mockMvc.perform(get("/books/{id}", bookId).with(user("testuser")))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(bookId.toString()))
      .andExpect(jsonPath("$.title").value("Dom Casmurro"))
      .andExpect(jsonPath("$.coverUrl").value("https://example.com/dom-casmurro.jpg")
    );
  }

  @Test
  void createBook_ShouldReturnCreatedBook_WhenValidRequest() throws Exception {
    UUID authorId = UUID.randomUUID();
    UUID genre1Id = UUID.randomUUID();
    UUID genre2Id = UUID.randomUUID();

    BookCreateDTO request = new BookCreateDTO(
      "Dom Casmurro",
      "A obra narra a vida de Bento Santiago...",
      256,
      authorId,
      List.of(genre1Id, genre2Id)
    );

    UUID savedBookId = UUID.randomUUID();
    BookDetailDTO savedDTO = new BookDetailDTO(
      savedBookId,
      "Dom Casmurro",
      "A obra narra a vida de Bento Santiago...",
      256,
      null, null, null, null, null
    );

    when(bookService.save(any(Book.class), eq(authorId), eq(List.of(genre1Id, genre2Id))))
      .thenReturn(savedDTO)
    ;

    mockMvc.perform(post("/books").with(user("testuser"))
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(savedBookId.toString()))
      .andExpect(jsonPath("$.title").value("Dom Casmurro")
    );
  }

  @Test
  void createBook_ShouldReturn400_WhenServiceThrows() throws Exception {
    UUID authorId = UUID.randomUUID();
    UUID genreId = UUID.randomUUID();

    BookCreateDTO request = new BookCreateDTO(
      null,
      "Sinopse",
      256,
      authorId,
      List.of(genreId)
    );

    when(bookService.save(any(Book.class), eq(authorId), eq(List.of(genreId))))
      .thenThrow(new IllegalArgumentException("Dados inválidos")
    );

    mockMvc.perform(post("/books").with(user("testuser"))
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest()
    );
  }

  @Test
  void updateBook_ShouldReturnUpdatedBook_WhenValidRequest() throws Exception {
    UUID bookId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    UUID genre1Id = UUID.randomUUID();
    UUID genre2Id = UUID.randomUUID();

    BookCreateDTO request = new BookCreateDTO(
      "Dom Casmurro - Updated",
      "Updated synopsis...",
      300,
      authorId,
      List.of(genre1Id, genre2Id)
    );

    BookDetailDTO updatedDTO = new BookDetailDTO(
      bookId,
      "Dom Casmurro - Updated",
      "Updated synopsis...",
      300,
      null, null, null, null, null
    );

    when(bookService.update(eq(bookId), any(Book.class), eq(authorId), eq(List.of(genre1Id, genre2Id))))
      .thenReturn(Optional.of(updatedDTO)
    );

    mockMvc.perform(put("/books/{id}", bookId).with(user("testuser"))
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.title").value("Dom Casmurro - Updated"))
      .andExpect(jsonPath("$.pageCount").value(300)
    );
  }

  @Test
  void updateBook_ShouldReturn404_WhenNotFound() throws Exception {
    UUID bookId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    UUID genreId = UUID.randomUUID();

    BookCreateDTO request = new BookCreateDTO(
      "Title",
      "Sinopse",
      200,
      authorId,
      List.of(genreId)
    );

    when(bookService.update(eq(bookId), any(Book.class), eq(authorId), eq(List.of(genreId))))
      .thenReturn(Optional.empty()
    );

    mockMvc.perform(put("/books/{id}", bookId).with(user("testuser"))
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isNotFound()
    );
  }

  @Test
  void patchBook_ShouldReturnUpdatedBook_WhenValidRequest() throws Exception {
    UUID bookId = UUID.randomUUID();

    BookCreateDTO request = new BookCreateDTO(
      "Novo Título",
      null,
      null,
      null,
      null
    );

    BookDetailDTO patchedDTO = new BookDetailDTO(
      bookId,
      "Novo T\u00edtulo",
      "A obra narra a vida de Bento Santiago...",
      256,
      null, null, null, null, null
    );

    when(bookService.partialUpdate(eq(bookId), any(Book.class), isNull(), isNull()))
      .thenReturn(Optional.of(patchedDTO)
    );

    mockMvc.perform(patch("/books/{id}", bookId).with(user("testuser"))
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.title").value("Novo Título")
    );
  }

  @Test
  void patchBook_ShouldReturn404_WhenNotFound() throws Exception {
    UUID bookId = UUID.randomUUID();

    BookCreateDTO request = new BookCreateDTO(
      "Novo",
      null,
      null,
      null,
      null
    );

    when(bookService.partialUpdate(eq(bookId), any(Book.class), isNull(), isNull()))
      .thenReturn(Optional.empty()
    );

    mockMvc.perform(patch("/books/{id}", bookId).with(user("testuser"))
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isNotFound()
    );
  }

  @Test
  void uploadCover_ShouldReturnUpdatedBook() throws Exception {
    UUID bookId = UUID.randomUUID();

    BookDetailDTO updatedDTO = new BookDetailDTO(
      bookId,
      "Dom Casmurro",
      null,
      null,
      null, null,
      "https://example.com/new-cover.jpg",
      null, null
    );

    MockMultipartFile cover = new MockMultipartFile(
      "cover",
      "cover.jpg",
      "image/jpeg",
      "fake".getBytes()
    );

    when(bookService.updateCover(eq(bookId), any())).thenReturn(Optional.of(updatedDTO));

    mockMvc.perform(multipart("/books/{id}/cover", bookId)
      .file(cover)
      .with(request -> {
        request.setMethod("PUT");
        return request;
      })
      .with(user("testuser")))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.coverUrl").value("https://example.com/new-cover.jpg")
    );
  }

  @Test
  void uploadCover_ShouldReturn404_WhenBookNotFound() throws Exception {
    UUID bookId = UUID.randomUUID();

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
      })
      .with(user("testuser")))
      .andExpect(status().isNotFound()
    );
  }

  @Test
  void deleteCover_ShouldReturnNoContent() throws Exception {
    UUID bookId = UUID.randomUUID();
    when(bookService.removeCover(bookId)).thenReturn(true);

    mockMvc.perform(delete("/books/{id}/cover", bookId).with(user("testuser")))
      .andExpect(status().isNoContent()
    );
  }

  @Test
  void deleteCover_ShouldReturn404_WhenBookNotFound() throws Exception {
    UUID bookId = UUID.randomUUID();
    when(bookService.removeCover(bookId)).thenReturn(false);

    mockMvc.perform(delete("/books/{id}/cover", bookId).with(user("testuser")))
      .andExpect(status().isNotFound()
    );
  }

  @Test
  void deleteBook_ShouldReturnNoContent_WhenDeleted() throws Exception {
    UUID bookId = UUID.randomUUID();
    when(bookService.deleteById(bookId)).thenReturn(true);

    mockMvc.perform(delete("/books/{id}", bookId).with(user("testuser")))
      .andExpect(status().isNoContent()
    );
  }

  @Test
  void deleteBook_ShouldReturn404_WhenNotFound() throws Exception {
    UUID bookId = UUID.randomUUID();
    when(bookService.deleteById(bookId)).thenReturn(false);

    mockMvc.perform(delete("/books/{id}", bookId).with(user("testuser")))
      .andExpect(status().isNotFound()
    );
  }

  @Test
  void searchByTitle_ShouldReturnPagedResult() throws Exception {
    BookDTO dto = new BookDTO(
      UUID.randomUUID(),
      "Dom Casmurro",
      null, null, null, null, null, null, null
    );
    Page<BookDTO> page = new PageImpl<>(List.of(dto));

    when(bookService.findByTitle(eq("Dom"), any(Pageable.class))).thenReturn(page);

    mockMvc.perform(get("/books").param("title", "Dom").with(user("testuser")))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.content[0].title").value("Dom Casmurro")
    );
  }

  @Test
  void getAllBooks_ShouldReturnPagedResult() throws Exception {
    BookDTO dto = new BookDTO(
      UUID.randomUUID(),
      "Dom Casmurro",
      null, null, null, null, null, null, null
    );
    Page<BookDTO> page = new PageImpl<>(List.of(dto));

    when(bookService.findAll(any(Pageable.class))).thenReturn(page);

    mockMvc.perform(get("/books").with(user("testuser")))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.content").isArray()
    );
  }
}