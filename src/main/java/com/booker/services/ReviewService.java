package com.booker.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.booker.DTO.Book.BookDetailDTO;
import com.booker.DTO.Review.CreateReviewDTO;
import com.booker.DTO.Review.UpdateReviewDTO;
import com.booker.exceptions.BusinessRuleException;
import com.booker.exceptions.ErrorCode;
import com.booker.exceptions.ResourceNotFoundException;
import com.booker.mappers.BookMapper;
import com.booker.mappers.ReviewMapper;
import com.booker.models.Book;
import com.booker.models.Review;
import com.booker.models.User;
import com.booker.repositories.BookRepository;
import com.booker.repositories.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service @Transactional @RequiredArgsConstructor
public class ReviewService {
  private final ReviewRepository repository;
  private final BookRepository bookRepository;
  private final BookService bookService;
  private final BookMapper bookMapper;
  private final ReviewMapper mapper;

  @Transactional(readOnly = true)
  public Page<Review> findAll(Pageable pageable) { return repository.findAll(pageable); }

  @Transactional(readOnly = true)
  public Review findById(UUID id) {
    return repository
      .findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Review not found for ID: " + id))
    ;
  }

  public Review create(CreateReviewDTO data, User currentUser) {
    BookDetailDTO bookDTO = bookService.findById(data.bookID());
    Book book = bookMapper.toEntity(bookDTO);

    Review review = mapper.toEntity(data, currentUser, book);

    try {
      Review savedReview = repository.save(review);

      recalculateBookRating(book.getId());

      return savedReview;
    }
    catch (DataIntegrityViolationException exception) {
      throw new BusinessRuleException("It's not allowed to create more than one review per book", ErrorCode.DUPLICATE_REVIEW);
    }
  }

  public void update(UUID id, UpdateReviewDTO data) {
    Review review = findById(id);

    if (data.score() != null) review.setScore(data.score());
    if (data.headline() != null) review.setHeadline(data.headline());
    if (data.text() != null) review.setText(data.text());

    repository.save(review);

    recalculateBookRating(review.getBook().getId());
  }

  public void delete(UUID id) {
    Review review = repository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Review not found for ID: " + id))
    ;

    UUID bookId = review.getBook().getId();

    repository.deleteById(id);
    repository.flush();

    recalculateBookRating(bookId);
  }

  @Transactional(readOnly = true)
  public Page<Review> findByBookID(UUID bookID, Pageable pageable) { return repository.findByBookId(bookID, pageable); }

  @Transactional(readOnly = true)
  public boolean isOwner(UUID id, String username) {
    return repository.findById(id)
      .map(review -> review.getUser().getUsername().equals(username))
      .orElse(false)
    ;
  }

  private void recalculateBookRating(UUID bookId) {
    Book book = bookRepository.findById(bookId)
      .orElseThrow(() -> new ResourceNotFoundException("Book not found for ID: " + bookId))
    ;

    BigDecimal avgScore = repository.findAverageScoreByBookId(bookId);
    long count = repository.countByBookId(bookId);

    book.setRating(avgScore != null ? avgScore.setScale(1, RoundingMode.HALF_UP) : BigDecimal.ZERO);
    book.setRatingsCount((int) count);

    bookRepository.save(book);
  }
}