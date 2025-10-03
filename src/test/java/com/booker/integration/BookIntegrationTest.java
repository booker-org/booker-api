package com.booker.integration;

import com.booker.dtos.BookCreateDTO;
import com.booker.entities.Author;
import com.booker.entities.Book;
import com.booker.entities.Genre;
import com.booker.repositories.AuthorRepository;
import com.booker.repositories.BookRepository;
import com.booker.repositories.GenreRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class BookIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private AuthorRepository authorRepository;

  @Autowired
  private GenreRepository genreRepository;

  private Author savedAuthor;
  private Genre savedGenre1;
  private Genre savedGenre2;

  @BeforeEach
  void setUp() {
    // Clear database before each test
    bookRepository.deleteAll();
    authorRepository.deleteAll();
    genreRepository.deleteAll();

    // Create base data for tests
    Author author = new Author();
    author.setName("Machado de Assis");
    author.setBiography("Considerado um dos maiores escritores brasileiros...");
    savedAuthor = authorRepository.save(author);

    Genre genre1 = new Genre();
    genre1.setName("Ficção");
    savedGenre1 = genreRepository.save(genre1);

    Genre genre2 = new Genre();
    genre2.setName("Clássico");
    savedGenre2 = genreRepository.save(genre2);
  }

  // ========== CREATE TESTS ==========

  @Test
  void createBook_ShouldReturnCreatedBook_WhenValidData() throws Exception {
    BookCreateDTO createRequest = new BookCreateDTO(
        "Dom Casmurro",
        "A obra narra a vida de Bento Santiago...",
        256,
        savedAuthor.getId(),
        List.of(savedGenre1.getId(), savedGenre2.getId()));

    mockMvc.perform(post("/books")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.title").value("Dom Casmurro"))
        .andExpect(jsonPath("$.synopsis").value("A obra narra a vida de Bento Santiago..."))
        .andExpect(jsonPath("$.pageCount").value(256))
        .andExpect(jsonPath("$.authorName").value("Machado de Assis"))
        .andExpect(jsonPath("$.genres").isArray())
        .andExpect(jsonPath("$.genres", hasSize(2)))
        .andExpect(jsonPath("$.genres", containsInAnyOrder("Ficção", "Clássico")))
        .andExpect(jsonPath("$.createdAt").isNotEmpty())
        .andExpect(jsonPath("$.updatedAt").isNotEmpty());

    List<Book> books = bookRepository.findAll();
    assert books.size() == 1;
    assert books.get(0).getTitle().equals("Dom Casmurro");
  }

  @Test
  void createBook_ShouldReturn400_WhenInvalidData() throws Exception {
    BookCreateDTO invalidRequest = new BookCreateDTO(
        "A",
        "Sinopse qualquer",
        256,
        savedAuthor.getId(),
        List.of(savedGenre1.getId()));

    mockMvc.perform(post("/books")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());

    List<Book> books = bookRepository.findAll();
    assert books.isEmpty();
  }

  @Test
  void createBook_ShouldReturn400_WhenAuthorNotExists() throws Exception {
    BookCreateDTO invalidRequest = new BookCreateDTO(
        "Título Válido",
        "Sinopse qualquer",
        256,
        999L,
        List.of(savedGenre1.getId()));

    mockMvc.perform(post("/books")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  // ========== READ TESTS ==========

  @Test
  void getBookById_ShouldReturnBook_WhenBookExists() throws Exception {
    // Given
    BookCreateDTO createRequest = new BookCreateDTO(
        "Dom Casmurro",
        "A obra narra a vida de Bento Santiago...",
        256,
        savedAuthor.getId(),
        List.of(savedGenre1.getId(), savedGenre2.getId()));

    // Create the book via API first
    String createResponse = mockMvc.perform(post("/books")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    Long bookId = objectMapper.readTree(createResponse).path("id").asLong();

    // When & Then
    mockMvc.perform(get("/books/{id}", bookId))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(bookId))
        .andExpect(jsonPath("$.title").value("Dom Casmurro"))
        .andExpect(jsonPath("$.author.name").value("Machado de Assis"))
        .andExpect(jsonPath("$.genres", hasSize(2)));
  }

  @Test
  void getBookById_ShouldReturn404_WhenBookNotExists() throws Exception {
    // When & Then
    mockMvc.perform(get("/books/{id}", 999L))
        .andExpect(status().isNotFound());
  }

  @Test
  void getAllBooks_ShouldReturnPageOfBooks() throws Exception {
    // Given
    BookCreateDTO book1Request = new BookCreateDTO(
        "Dom Casmurro",
        "Uma história de amor",
        256,
        savedAuthor.getId(),
        List.of(savedGenre1.getId()));

    BookCreateDTO book2Request = new BookCreateDTO(
        "O Cortiço",
        "Aventuras no espaço",
        300,
        savedAuthor.getId(),
        List.of(savedGenre2.getId()));

    // Create books via API
    mockMvc.perform(post("/books")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(book1Request)))
        .andExpect(status().isCreated());

    mockMvc.perform(post("/books")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(book2Request)))
        .andExpect(status().isCreated());

    // When & Then
    mockMvc.perform(get("/books")
        .param("size", "10")
        .param("page", "0"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content", hasSize(2)))
        .andExpect(jsonPath("$.totalElements").value(2))
        .andExpect(jsonPath("$.totalPages").value(1));
  }

  // ========== UPDATE TESTS ==========

  @Test
  void updateBook_ShouldReturnUpdatedBook_WhenValidData() throws Exception {
    // Given - Create a book first
    BookCreateDTO createRequest = new BookCreateDTO(
        "Dom Casmurro Original",
        "Sinopse original",
        256,
        savedAuthor.getId(),
        List.of(savedGenre1.getId()));

    String createResponse = mockMvc.perform(post("/books")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    Long bookId = objectMapper.readTree(createResponse).path("id").asLong();

    BookCreateDTO updateRequest = new BookCreateDTO(
        "Dom Casmurro - Edição Revisada",
        "Nova sinopse atualizada",
        300,
        savedAuthor.getId(),
        List.of(savedGenre1.getId()));

    // When & Then
    mockMvc.perform(put("/books/{id}", bookId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(bookId))
        .andExpect(jsonPath("$.title").value("Dom Casmurro - Edição Revisada"))
        .andExpect(jsonPath("$.synopsis").value("Nova sinopse atualizada"))
        .andExpect(jsonPath("$.pageCount").value(300))
        .andExpect(jsonPath("$.genres", hasSize(1)));
  }

  @Test
  void updateBook_ShouldReturn404_WhenBookNotExists() throws Exception {
    // Given
    BookCreateDTO updateRequest = new BookCreateDTO(
        "Título Qualquer",
        "Sinopse qualquer",
        256,
        savedAuthor.getId(),
        List.of(savedGenre1.getId()));

    // When & Then
    mockMvc.perform(put("/books/{id}", 999L)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isNotFound());
  }

  // ========== DELETE TESTS ==========

  @Test
  void deleteBook_ShouldReturnNoContent_WhenBookExists() throws Exception {
    // Given - Criar um livro primeiro
    BookCreateDTO createRequest = new BookCreateDTO(
        "Dom Casmurro",
        "A obra narra a vida de Bento Santiago...",
        256,
        savedAuthor.getId(),
        List.of(savedGenre1.getId()));

    String createResponse = mockMvc.perform(post("/books")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    Long bookId = objectMapper.readTree(createResponse).path("id").asLong();

    // When & Then
    mockMvc.perform(delete("/books/{id}", bookId))
        .andExpect(status().isNoContent());

    // Verify it was deleted from the database
    assert bookRepository.findById(bookId).isEmpty();
  }

  @Test
  void deleteBook_ShouldReturn404_WhenBookNotExists() throws Exception {
    // When & Then
    mockMvc.perform(delete("/books/{id}", 999L))
        .andExpect(status().isNotFound());
  }
}
