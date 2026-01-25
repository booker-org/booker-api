package com.booker.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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

@Service @Transactional
public class ReviewService {
  @Autowired
  private ReviewRepository repository;

  @Autowired
  private UserService userService;

  @Autowired
  private BookService bookService;

  @Autowired
  private BookMapper bookMapper;

  @Autowired
  private ReviewMapper mapper;

  @Transactional(readOnly = true)
  public Page<Review> findAll(Pageable pageable) { return repository.findAll(pageable); }

  @Transactional(readOnly = true)
  public Review findById(UUID id) {
    return repository
      .findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Review not found for ID: " + id))
    ;
  }

  public Review create(CreateReviewDTO data) {
    User user = userService.findById(data.userID());
    BookDetailDTO bookDTO = bookService.findById(data.bookID());
    Book book = bookMapper.toEntity(bookDTO);

    Review review = mapper.toEntity(data, user, book);

    try { repository.save(review); }
    catch (DataIntegrityViolationException exception) {
      throw new BusinessRuleException("It's not allowed to create more than one review per book");
    }

    return repository.save(review);
  }

  public void update(UUID id, UpdateReviewDTO data) {
    Review review = findById(id);

    review.setScore(data.score());
    review.setHeadline(data.headline());
    review.setText(data.text());

    repository.save(review);
  }

  public void delete(UUID id) { repository.deleteById(id); }
}