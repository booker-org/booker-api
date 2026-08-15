package com.booker.services;

import java.math.BigDecimal;
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

      bookRepository.adjustRating(book.getId(), review.getScore(), 1);

      return savedReview;
    }
    catch (DataIntegrityViolationException exception) {
      throw new BusinessRuleException("It's not allowed to create more than one review per book", ErrorCode.DUPLICATE_REVIEW);
    }
  }

  public void update(UUID id, UpdateReviewDTO data) {
    Review review = findById(id);
    BigDecimal oldScore = review.getScore();

    if (data.score() != null) review.setScore(data.score());
    if (data.headline() != null) review.setHeadline(data.headline());
    if (data.text() != null) review.setText(data.text());

    repository.save(review);

    if (data.score() != null) {
      bookRepository.adjustRating(review.getBook().getId(), data.score().subtract(oldScore), 0);
    }
  }

  public void delete(UUID id) {
    Review review = repository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Review not found for ID: " + id))
    ;

    UUID bookId = review.getBook().getId();

    repository.deleteById(id);
    repository.flush();

    bookRepository.adjustRating(bookId, review.getScore().negate(), -1);
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
}