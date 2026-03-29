package com.booker.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

import com.booker.DTO.Book.BookDetailDTO;
import com.booker.DTO.Review.CreateReviewDTO;
import com.booker.DTO.Review.UpdateReviewDTO;
import com.booker.exceptions.BusinessRuleException;
import com.booker.exceptions.ResourceNotFoundException;
import com.booker.fixtures.ReviewFixtures;
import com.booker.mappers.BookMapper;
import com.booker.mappers.ReviewMapper;
import com.booker.models.Book;
import com.booker.models.Review;
import com.booker.models.User;
import com.booker.repositories.ReviewRepository;

import static com.booker.constants.Tests.DEFAULT_TEST_ID;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {
  @InjectMocks
  private ReviewService service;

  @Mock
  private ReviewRepository repository;

  @Mock
  private BookService bookService;

  @Mock
  private BookMapper bookMapper;

  @Mock
  private ReviewMapper mapper;

  @Test @DisplayName("Should retrieve a page of reviews")
  void findAll_ShouldReturnPageOfReviews() {
    // Given
    Pageable pageable = mock(Pageable.class);
    Page<Review> page = Page.empty();

    given(repository.findAll(pageable)).willReturn(page);

    // When
    Page<Review> result = service.findAll(pageable);

    // Then
    assertNotNull(result);

    then(repository).should().findAll(pageable);
  }

  @Test @DisplayName("Should retrieve a review given an id")
  void findById_ShouldReturnReview_WhenReviewExists() {
    // Given
    Review review = mock(Review.class);

    given(repository.findById(DEFAULT_TEST_ID)).willReturn(Optional.of(review));

    // When
    Review result = service.findById(DEFAULT_TEST_ID);

    // Then
    assertNotNull(result);

    then(repository).should().findById(DEFAULT_TEST_ID);
  }

  @Test @DisplayName("Should throw error if review not found")
  void findById_ShouldThrowException_WhenReviewNotFound() {
    // Given
    given(repository.findById(DEFAULT_TEST_ID)).willReturn(Optional.empty());

    // When / Then
    assertThrows(ResourceNotFoundException.class, () -> service.findById(DEFAULT_TEST_ID));
  }

  @Test @DisplayName("Should create a review given valid data")
  void create_ShouldReturnReview_WhenValidData() {
    // Given
    CreateReviewDTO data = ReviewFixtures.validCreateReviewDTO();
    User currentUser = mock(User.class);
    BookDetailDTO bookDTO = mock(BookDetailDTO.class);
    Book book = mock(Book.class);
    Review review = ReviewFixtures.validReview();

    given(bookService.findById(data.bookID())).willReturn(bookDTO);
    given(bookMapper.toEntity(bookDTO)).willReturn(book);
    given(mapper.toEntity(data, currentUser, book)).willReturn(review);
    given(repository.save(review)).willReturn(review);

    // When
    Review result = service.create(data, currentUser);

    // Then
    assertAll(
      () -> assertSame(review, result),
      () -> assertEquals(data.score(), result.getScore()),
      () -> assertEquals(data.headline(), result.getHeadline()),
      () -> assertEquals(data.text(), result.getText()),
      () -> assertEquals(data.bookID(), result.getBook().getId())
    );

    then(repository).should().save(review);
  }

  @Test @DisplayName("Should throw error if already exists a review made by that user for that book")
  void create_ShouldThrowException_WhenReviewAlreadyExistsForUserAndBook() {
    // Given
    CreateReviewDTO data = ReviewFixtures.validCreateReviewDTO();
    User currentUser = mock(User.class);
    BookDetailDTO bookDTO = mock(BookDetailDTO.class);
    Book book = mock(Book.class);
    Review review = ReviewFixtures.validReview();

    given(bookService.findById(data.bookID())).willReturn(bookDTO);
    given(bookMapper.toEntity(bookDTO)).willReturn(book);
    given(mapper.toEntity(data, currentUser, book)).willReturn(review);
    given(repository.save(review)).willThrow(DataIntegrityViolationException.class);

    // When / Then
    assertThrowsExactly(BusinessRuleException.class, () -> service.create(data, currentUser));
  }

  @Test @DisplayName("Should update a review given valid data - All fields")
  void update_ShouldUpdateAllFields_WhenValidData() {
    // Given
    final BigDecimal newScore = BigDecimal.TEN;
    final String newHeadline = "New";
    final String newText = "New text";

    Review review = new Review();
    review.setScore(BigDecimal.ONE);
    review.setHeadline("Old");
    review.setText("Old text");

    final UpdateReviewDTO data = new UpdateReviewDTO(newScore, newHeadline, newText);

    given(repository.findById(DEFAULT_TEST_ID)).willReturn(Optional.of(review));

    // When
    service.update(DEFAULT_TEST_ID, data);

    // Then
    assertEquals(newScore, review.getScore());
    assertEquals(newHeadline, review.getHeadline());
    assertEquals(newText, review.getText());

    then(repository).should().save(review);
  }

  @Test @DisplayName("Should update a review given valid data - Only given fields")
  void update_ShouldUpdateOnlyProvidedFields_WhenPartialData() {
    // Given
    final String oldHeadline = "Old";
    final String oldText = "Old text";
    final BigDecimal newScore = BigDecimal.TEN;

    Review review = new Review();
    review.setScore(BigDecimal.ONE);
    review.setHeadline(oldHeadline);
    review.setText(oldText);

    final UpdateReviewDTO data = new UpdateReviewDTO(newScore, null, null);

    given(repository.findById(DEFAULT_TEST_ID)).willReturn(Optional.of(review));

    // When
    service.update(DEFAULT_TEST_ID, data);

    // Then
    assertEquals(newScore, review.getScore());
    assertEquals(oldHeadline, review.getHeadline());
    assertEquals(oldText, review.getText());

    then(repository).should().save(review);
  }

  @Test @DisplayName("Should not modify a review when all values are null")
  void update_ShouldKeepValuesUnchanged_WhenAllFieldsAreNull() {
    // Given
    final BigDecimal oldScore = BigDecimal.ONE;
    final String oldHeadline = "Old";
    final String oldText = "Old text";

    Review review = new Review();
    review.setScore(oldScore);
    review.setHeadline(oldHeadline);
    review.setText(oldText);

    final UpdateReviewDTO data = new UpdateReviewDTO(null, null, null);

    given(repository.findById(DEFAULT_TEST_ID)).willReturn(Optional.of(review));

    // When
    service.update(DEFAULT_TEST_ID, data);

    // Then
    assertEquals(oldScore, review.getScore());
    assertEquals(oldHeadline, review.getHeadline());
    assertEquals(oldText, review.getText());

    then(repository).should().save(review);
  }

  @Test @DisplayName("Should throw exception when review not found")
  void update_ShouldThrowException_WhenReviewNotFound() {
    // Given
    UpdateReviewDTO data = new UpdateReviewDTO(BigDecimal.ONE, "New", "New text");

    given(repository.findById(DEFAULT_TEST_ID)).willReturn(Optional.empty());

    // When / Then
    assertThrows(ResourceNotFoundException.class, () -> service.update(DEFAULT_TEST_ID, data));
  }

  @Test @DisplayName("Should delete review if it exists")
  void delete_ShouldDeleteReview_WhenReviewExists() {
    // Given
    given(repository.existsById(DEFAULT_TEST_ID)).willReturn(true);

    // When
    service.delete(DEFAULT_TEST_ID);

    // Then
    then(repository).should().deleteById(DEFAULT_TEST_ID);
  }

  @Test @DisplayName("Should throw error to delete review if it doesn't exists")
  void delete_ShouldThrowException_WhenReviewDoesNotExist() {
    // Given
    given(repository.existsById(DEFAULT_TEST_ID)).willReturn(false);

    // When / Then
    assertThrows(ResourceNotFoundException.class, () -> service.delete(DEFAULT_TEST_ID));

    then(repository).should(never()).deleteById(DEFAULT_TEST_ID);
  }

  @Test @DisplayName("Should retrieve a page of reviews given an book id")
  void findByBookID_ShouldReturnPageOfReviews_WhenBookIdIsValid() {
    // Given
    Pageable pageable = mock(Pageable.class);
    Review review = new Review();
    List<Review> reviews = List.of(review);
    Page<Review> page = new PageImpl<>(reviews, pageable, reviews.size());

    given(repository.findByBookId(DEFAULT_TEST_ID, pageable)).willReturn(page);

    // When
    Page<Review> result = service.findByBookID(DEFAULT_TEST_ID, pageable);

    // Then
    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    assertEquals(review, result.getContent().get(0));

    then(repository).should().findByBookId(DEFAULT_TEST_ID, pageable);
  }

  @Test @DisplayName("Should return true if the user is owner of that review given it's username")
  void isOwner_ShouldReturnTrue_WhenUserIsOwner() {
    // Given
    final String username = "user";

    User user = new User();
    user.setUsername(username);

    Review review = new Review();
    review.setUser(user);

    given(repository.findById(DEFAULT_TEST_ID)).willReturn(Optional.of(review));

    // When
    boolean result = service.isOwner(DEFAULT_TEST_ID, username);

    // Then
    assertTrue(result);
  }

  @Test @DisplayName("Should return false if the user is not owner of that review given it's username")
  void isOwner_ShouldReturnFalse_WhenUserIsNotOwner() {
    // Given
    User user = new User();
    user.setUsername("anotherUser");

    Review review = new Review();
    review.setUser(user);

    given(repository.findById(DEFAULT_TEST_ID)).willReturn(Optional.of(review));

    // When
    boolean result = service.isOwner(DEFAULT_TEST_ID, "user");

    // Then
    assertFalse(result);
  }

  @Test @DisplayName("Should return false if review not found")
  void isOwner_ShouldReturnFalse_WhenReviewNotFound() {
    // Given
    given(repository.findById(DEFAULT_TEST_ID)).willReturn(Optional.empty());

    // When
    boolean result = service.isOwner(DEFAULT_TEST_ID, "user");

    // Then
    assertFalse(result);
  }
}