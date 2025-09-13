package com.booker.repositories;

import com.booker.models.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Book createAndSaveBook(String title, String synopsis, Long authorId) {
        Book book = new Book();
        book.setTitle(title);
        book.setSynopsis(synopsis);
        book.setPageCount(200);
        book.setAuthorId(authorId);
        book.setCoverUrl("https://example.com/" + title.toLowerCase().replace(" ", "-") + ".jpg");
        return entityManager.persistAndFlush(book);
    }

    // ========== TESTS ==========

    @Test
    void findByTitleContainingIgnoreCase_ShouldReturnBooksMatchingTitle_WhenTitleExists() {
        // Given
        createAndSaveBook("Dom Casmurro", "Romance brasileiro", 1L);
        createAndSaveBook("O Cortiço", "Romance naturalista", 1L);
        createAndSaveBook("Machado de Assis Biography", "Biografia", 2L);

        Pageable pageable = PageRequest.of(0, 10);

        // When - Searching with different case
        Page<Book> result = bookRepository.findByTitleContainingIgnoreCase("dom", pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Dom Casmurro");
    }

    @Test
    void findByTitleContainingIgnoreCase_ShouldBeCaseInsensitive() {
        // Given
        createAndSaveBook("Dom Casmurro", "Romance brasileiro", 1L);

        Pageable pageable = PageRequest.of(0, 10);

        // When - Searching with different case
        Page<Book> result = bookRepository.findByTitleContainingIgnoreCase("DOM", pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Dom Casmurro");
    }

    @Test
    void findByAuthorId_ShouldReturnBooksFromSpecificAuthor_WhenAuthorExists() {
        // Given
        createAndSaveBook("Dom Casmurro", "Romance", 1L);
        createAndSaveBook("Memórias Póstumas", "Romance", 1L);
        createAndSaveBook("O Cortiço", "Romance", 2L);

        Pageable pageable = PageRequest.of(0, 10);

        // When - Searching for books by authorId 1L
        Page<Book> result = bookRepository.findByAuthorId(1L, pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(Book::getAuthorId)
                .containsOnly(1L);
    }

    @Test
    void findByTitleOrSynopsisContaining_ShouldSearchInBothFields() {
        // Given
        createAndSaveBook("Dom Casmurro", "Romance sobre ciúme", 1L);
        createAndSaveBook("O Cortiço", "Romance naturalista sobre moradia", 2L);
        createAndSaveBook("Helena", "Drama familiar", 1L);

        Pageable pageable = PageRequest.of(0, 10);

        // When - Searching for "moradia" which is in the synopsis of "O Cortiço"
        Page<Book> result = bookRepository.findByTitleOrSynopsisContaining("moradia", pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("O Cortiço");
    }

    @Test
    void findByTitleOrSynopsisContaining_ShouldReturnEmptyPage_WhenNoMatch() {
        // Given
        createAndSaveBook("Dom Casmurro", "Romance brasileiro", 1L);

        Pageable pageable = PageRequest.of(0, 10);

        // When - Searching for a term that doesn't exist
        Page<Book> result = bookRepository.findByTitleOrSynopsisContaining("ficção científica", pageable);

        // Then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }
}
