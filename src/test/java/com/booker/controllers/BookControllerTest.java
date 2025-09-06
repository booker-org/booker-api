package com.booker.controllers;

import com.booker.models.Book;
import com.booker.services.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    private Book createBaseBook() {
        Book book = new Book();
        book.setTitle("Dom Casmurro");
        book.setSynopsis("A obra narra a vida de Bento Santiago...");
        book.setPageCount(256);
        book.setAuthorId(1L);
        book.setCoverUrl("https://example.com/dom-casmurro.jpg");
        return book;
    }

    // SUCCESS CASES

    // ========== GET TESTS ==========

    @Test
    public void shouldReturnBookWhenFindingByExistingId() throws Exception {
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
                .andExpect(jsonPath("$.authorId").value(1))
                .andExpect(jsonPath("$.coverUrl").value("https://example.com/dom-casmurro.jpg"));
    }

    // ========== PUT TESTS ==========

    @Test
    public void shouldCreateNewBookSuccessfully() throws Exception {
        // Given - Request without ID
        Book newBookRequest = createBaseBook();

        // Given - Response with generated ID
        Book savedBook = createBaseBook();
        savedBook.setId(1L);

        // When
        when(bookService.save(any(Book.class))).thenReturn(savedBook);

        // Then
        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newBookRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Dom Casmurro"))
                .andExpect(jsonPath("$.pageCount").value(256));
    }

    // ========== UPDATE TESTS ==========

    @Test
    public void shouldUpdateExistingBookSuccessfully() throws Exception {
        // Given - Existing book ID and update data
        Long bookId = 1L;
        Book updateRequest = createBaseBook();
        updateRequest.setTitle("Dom Casmurro - Updated");
        updateRequest.setSynopsis("Updated synopsis...");
        updateRequest.setPageCount(300);
        updateRequest.setCoverUrl("https://example.com/dom-casmurro-updated.jpg");

        Book expectedResult = createBaseBook();
        expectedResult.setId(bookId);
        expectedResult.setTitle("Dom Casmurro - Updated");
        expectedResult.setSynopsis("Updated synopsis...");
        expectedResult.setPageCount(300);
        expectedResult.setCoverUrl("https://example.com/dom-casmurro-updated.jpg");

        // When - Mock retorna o resultado esperado
        when(bookService.update(bookId, updateRequest)).thenReturn(Optional.of(expectedResult));

        // Then
        mockMvc.perform(put("/books/{id}", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(bookId))
                .andExpect(jsonPath("$.title").value("Dom Casmurro - Updated"))
                .andExpect(jsonPath("$.pageCount").value(300));
    }

    // ========== PATCH TESTS ==========

    @Test
    public void shouldUpdateFieldsSuccessfully() throws Exception {
        // Given - Existing book ID and partial update data
        Long bookId = 1L;
        Book updateRequest = new Book();
        updateRequest.setTitle("Novo Título");
        updateRequest.setPageCount(400);

        Book existingBook = createBaseBook();
        existingBook.setId(bookId);

        Book expectedResult = createBaseBook();
        expectedResult.setId(bookId);
        expectedResult.setTitle("Novo Título");
        expectedResult.setPageCount(400);

        // When - Mock behavior
        when(bookService.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(bookService.update(bookId, existingBook)).thenReturn(Optional.of(expectedResult));

        // Then
        mockMvc.perform(patch("/books/{id}", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(bookId))
                .andExpect(jsonPath("$.title").value("Novo Título"))
                .andExpect(jsonPath("$.pageCount").value(400));
    }


    // ========== DELETE TESTS ==========

    @Test
    public void shouldDeleteExistingBookSuccessfully() throws Exception {
        // Given - Existing book ID
        Long bookId = 1L;

        // When - Mock confirma que o livro foi deletado
        when(bookService.deleteById(bookId)).thenReturn(true);

        // Then
        mockMvc.perform(delete("/books/{id}", bookId))
                .andExpect(status().isNoContent());
    }

    // FAILURE CASES - VALIDATION

    // ========== GET TESTS ==========

    @Test
    public void shouldReturn404WhenFindingByNonExistingId() throws Exception {
        // Given
        Long nonExistentBookId = 999L;

        // When
        when(bookService.findById(nonExistentBookId)).thenReturn(Optional.empty());

        // Then
        mockMvc.perform(get("/books/{id}", nonExistentBookId))
                .andExpect(status().isNotFound());
    }

    // ========== PUT TESTS ==========

    @Test
    public void shouldReturn400WhenCreatingBookWithNullRequest() throws Exception {
        // Given - Null request

        // When
        when(bookService.save(any(Book.class))).thenThrow(new IllegalArgumentException("Dados do livro inválidos"));

        // When - Then
        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturn400WhenCreatingBookWithEmptyRequest() throws Exception {
        // Given - Empty request
        Book emptyBookRequest = new Book();

        // When
        when(bookService.save(any(Book.class))).thenThrow(new IllegalArgumentException("Dados do livro inválidos"));

        // Then
        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyBookRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturn400WhenCreatingBookWithInvalidTitle() throws Exception {
        // Given - Request with missing required fields (e.g., title)
        Book invalidBookRequest = createBaseBook();
        invalidBookRequest.setTitle(null); // Remove title

        // When
        when(bookService.save(any(Book.class))).thenThrow(new IllegalArgumentException("Título é obrigatório"));

        // Then
        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidBookRequest)))
                .andExpect(status().isBadRequest());

        // Additional invalid case
        invalidBookRequest.setTitle("A"); // Too short title

        when(bookService.save(any(Book.class)))
                .thenThrow(new IllegalArgumentException("Título deve ter entre 2 e 100 caracteres"));

        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidBookRequest)))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void shouldReturn400WhenCreatingBookWithInvalidPageCount() throws Exception {
        // Given - Request with invalid page count
        Book invalidBookRequest = createBaseBook();
        invalidBookRequest.setPageCount(-10); // Invalid page count

        // When
        when(bookService.save(any(Book.class)))
                .thenThrow(new IllegalArgumentException("Número de páginas deve ser positivo"));

        // Then
        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidBookRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturn400WhenCreatingBookWithInvalidAuthorId() throws Exception {
        // Given - Request with invalid author ID
        Book invalidBookRequest = createBaseBook();
        invalidBookRequest.setAuthorId(-1L); // Invalid author ID

        // When
        when(bookService.save(any(Book.class))).thenThrow(new IllegalArgumentException("ID do autor deve ser válido"));

        // Then
        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidBookRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturn400WhenCreatingBookWithInvalidCoverUrl() throws Exception {
        // Given - Request with invalid cover URL
        Book invalidBookRequest = createBaseBook();
        invalidBookRequest.setCoverUrl("invalid-url"); // Invalid URL

        // When
        when(bookService.save(any(Book.class))).thenThrow(new IllegalArgumentException("URL da capa deve ser válida"));

        // Then
        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidBookRequest)))
                .andExpect(status().isBadRequest());
    }

    // ========== UPDATE TESTS ==========

    @Test
    public void shouldReturn404WhenUpdatingNonExistingBook() throws Exception {
        // Given - Non-existing book ID
        Long nonExistentBookId = 999L;
        Book updateRequest = createBaseBook();
        updateRequest.setTitle("Updated Title");

        // When
        when(bookService.update(nonExistentBookId, updateRequest)).thenReturn(Optional.empty());

        // Then
        mockMvc.perform(put("/books/{id}", nonExistentBookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturn400WhenUpdatingBookWithInvalidData() throws Exception {
        // Given
        Long bookId = 1L;
        Book invalidUpdateRequest = createBaseBook();
        invalidUpdateRequest.setTitle(""); // Invalid title

        // When
        when(bookService.update(bookId, invalidUpdateRequest))
                .thenThrow(new IllegalArgumentException("Título deve ter entre 2 e 100 caracteres"));

        // Then
        mockMvc.perform(put("/books/{id}", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUpdateRequest)))
                .andExpect(status().isBadRequest());
    }

    // ========== PATCH TESTS ==========

    @Test
    public void shouldReturn404WhenPatchingNonExistingBook() throws Exception {
        // Given - Non-existing book ID
        Long nonExistentBookId = 999L;
        Book patchRequest = new Book();
        patchRequest.setTitle("Updated Title");

        // When
        when(bookService.findById(nonExistentBookId)).thenReturn(Optional.empty());
        when(bookService.update(nonExistentBookId, patchRequest)).thenReturn(Optional.empty());

        // Then
        mockMvc.perform(patch("/books/{id}", nonExistentBookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patchRequest)))
                .andExpect(status().isNotFound());
    }

    // ========== DELETE TESTS ==========

    @Test
    public void shouldReturn404WhenDeletingNonExistingBook() throws Exception {
        // Given - Non-existing book ID
        Long nonExistentBookId = 999L;

        // When
        when(bookService.deleteById(nonExistentBookId)).thenReturn(false);

        // Then
        mockMvc.perform(delete("/books/{id}", nonExistentBookId))
                .andExpect(status().isNotFound());
    }

    // ========== NEXT TESTS FOR YOU TO IMPLEMENT ==========

    /*
     * TODO: Implement the following tests:
     * 
     * 2. PUT /books/999 - Try to update non-existing book
     * 4. DELETE /books/999 - Try to delete non-existing book
     * 5. GET /books - List all books (with pagination)
     * 6. GET /books?title=Dom - Search by filter
     * 7. POST /books - Create book with invalid data (validation)
     */

}
