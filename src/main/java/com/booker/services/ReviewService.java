package com.booker.services;

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
import com.booker.exceptions.ResourceNotFoundException;
import com.booker.mappers.BookMapper;
import com.booker.mappers.ReviewMapper;
import com.booker.models.Book;
import com.booker.models.Review;
import com.booker.models.User;
import com.booker.repositories.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service @Transactional @RequiredArgsConstructor
public class ReviewService {
  private final ReviewRepository repository;
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

    try { return repository.save(review); }
    catch (DataIntegrityViolationException exception) {
      throw new BusinessRuleException("It's not allowed to create more than one review per book");
    }
  }

  public void update(UUID id, UpdateReviewDTO data) {
    Review review = findById(id);

    if (data.score() != null) review.setScore(data.score());
    if (data.headline() != null) review.setHeadline(data.headline());
    if (data.text() != null) review.setText(data.text());

    repository.save(review);
  }

  public void delete(UUID id) {
    if (!repository.existsById(id)) throw new ResourceNotFoundException("Review not found for ID: " + id);

    repository.deleteById(id);
  }

  @Transactional(readOnly = true)
  public boolean isOwner(UUID id, String username) {
    return repository.findById(id)
      .map(review -> review.getUser().getUsername().equals(username))
      .orElse(false)
    ;
  }
}