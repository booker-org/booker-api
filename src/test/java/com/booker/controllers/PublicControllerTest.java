package com.booker.controllers;

import java.util.List;
import java.util.UUID;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.booker.config.security.JwtAuthenticationFilter;
import com.booker.config.security.SecurityConfig;
import com.booker.DTO.Book.BookDTO;
import com.booker.DTO.Book.BookDetailDTO;
import com.booker.mappers.GenreMapper;
import com.booker.models.Genre;
import com.booker.services.BookService;
import com.booker.services.GenreService;
import com.booker.services.JwtService;

import static com.booker.constants.Domain.PUBLIC_ENDPOINT;

@WebMvcTest(
  controllers = PublicController.class,
  includeFilters = @ComponentScan.Filter(
    type = FilterType.ASSIGNABLE_TYPE,
    classes = { GenreMapper.class }
  )
)
@Import({ SecurityConfig.class, JwtAuthenticationFilter.class })
@ActiveProfiles("test")
class PublicControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private JwtService jwtService;

  @MockitoBean
  private UserDetailsService userDetailsService;

  @MockitoBean
  private PasswordEncoder passwordEncoder;

  @MockitoBean
  private BookService bookService;

  @MockitoBean
  private GenreService genreService;

  @Test
  void getAllBooks_ShouldReturnPagedResult() throws Exception {
    BookDTO dto = new BookDTO(
      UUID.randomUUID(),
      "Dom Casmurro",
      null, null, null, null, null, null, null
    );
    Page<BookDTO> page = new PageImpl<>(List.of(dto));

    when(bookService.findAll(any(Pageable.class))).thenReturn(page);

    mockMvc.perform(get(PUBLIC_ENDPOINT + "/books"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.content").isArray()
    );
  }

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

    mockMvc.perform(get(PUBLIC_ENDPOINT + "/books/{id}", bookId))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(bookId.toString()))
      .andExpect(jsonPath("$.title").value("Dom Casmurro"))
      .andExpect(jsonPath("$.coverUrl").value("https://example.com/dom-casmurro.jpg")
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

    mockMvc.perform(get(PUBLIC_ENDPOINT + "/books").param("title", "Dom"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.content[0].title").value("Dom Casmurro")
    );
  }

  @Test
  void getAllGenres_ShouldReturnPagedResult() throws Exception {
    Genre genre = new Genre();
    genre.setId(UUID.randomUUID());
    genre.setName("Ficção");

    Page<Genre> page = new PageImpl<>(List.of(genre));

    when(genreService.findAll(any(Pageable.class))).thenReturn(page);

    mockMvc.perform(get(PUBLIC_ENDPOINT + "/genres"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.content").isArray())
      .andExpect(jsonPath("$.content[0].name").value("Ficção")
    );
  }
}