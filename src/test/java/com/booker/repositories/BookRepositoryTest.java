package com.booker.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import com.booker.config.JpaConfig;
import com.booker.entities.Author;
import com.booker.entities.Book;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Author createBaseAuthor() {
        Author author = new Author();
        author.setName("Machado de Assis");
        author.setBiography("Considerado um dos maiores escritores brasileiros...");
        return author;
    }

    private Author createAndSaveAuthor() {
        Author author = createBaseAuthor();
        return entityManager.persistAndFlush(author);
    }

    private Book createAndSaveBookWithAuthor(String title, String synopsis, Author author) {
        Book book = new Book();
        book.setTitle(title);
        book.setSynopsis(synopsis);
        book.setPageCount(200);
        book.setAuthor(author);
        book.setCoverUrl("https://example.com/" + title.toLowerCase().replace(" ", "-") + ".jpg");
        return entityManager.persistAndFlush(book);
    }

    private Book createAndSaveBook(String title, String synopsis, Long authorId) {
        Author author = createAndSaveAuthor();
        
        Book book = new Book();
        book.setTitle(title);
        book.setSynopsis(synopsis);
        book.setPageCount(200);
        book.setAuthor(author);
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
        Author author1 = createAndSaveAuthor();
        Author author2 = createAndSaveAuthor();
        
        createAndSaveBookWithAuthor("Dom Casmurro", "Romance", author1);
        createAndSaveBookWithAuthor("Memórias Póstumas", "Romance", author1);
        createAndSaveBookWithAuthor("O Cortiço", "Romance", author2);

        Pageable pageable = PageRequest.of(0, 10);

        // When - Searching for books by author1's ID
        Page<Book> result = bookRepository.findByAuthorId(author1.getId(), pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(book -> book.getAuthor().getId())
                .containsOnly(author1.getId());
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

    @Test
    void findById_ShouldReturnBook_WhenBookExists() {
        // Given
        Book savedBook = createAndSaveBook("Dom Casmurro", "Romance brasileiro", 1L);

        // When
        Book result = bookRepository.findById(savedBook.getId()).orElse(null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Dom Casmurro");
    }


    @Test
    void findAll_ShouldReturnPageOfBooks() {
        // Given
        createAndSaveBook("Dom Casmurro", "Romance", 1L);
        createAndSaveBook("O Cortiço", "Romance", 1L);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Book> result = bookRepository.findAll(pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
    }


    @Test
    void save_ShouldPersistBook_WhenValidBook() {
        // Given
        Author author = createAndSaveAuthor();
        
        Book book = new Book();
        book.setTitle("Dom Casmurro");
        book.setSynopsis("Romance brasileiro");
        book.setPageCount(256);
        book.setAuthor(author);

        // When
        Book savedBook = bookRepository.save(book);

        // Then
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("Dom Casmurro");
    }

    @Test
    void deleteById_ShouldRemoveBook_WhenBookExists() {
        // Given
        Book savedBook = createAndSaveBook("Dom Casmurro", "Romance", 1L);
        Long bookId = savedBook.getId();

        // When
        bookRepository.deleteById(bookId);

        // Then
        assertThat(bookRepository.findById(bookId)).isEmpty();
    }

    // ========== SEARCH TESTS ==========

    @Test
    void findByAuthorId_ShouldReturnEmptyPage_WhenAuthorHasNoBooks() {
        // Given
        createAndSaveBook("Dom Casmurro", "Romance", 1L);

        Pageable pageable = PageRequest.of(0, 10);

        // When - Searching for books by non-existent author
        Page<Book> result = bookRepository.findByAuthorId(999L, pageable);

        // Then
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void findByTitleContainingIgnoreCase_ShouldReturnEmptyPage_WhenNoMatch() {
        // Given
        createAndSaveBook("Dom Casmurro", "Romance brasileiro", 1L);

        Pageable pageable = PageRequest.of(0, 10);

        // When - Searching for non-existent title
        Page<Book> result = bookRepository.findByTitleContainingIgnoreCase("inexistente", pageable);

        // Then
        assertThat(result.getContent()).isEmpty();
    }
}
