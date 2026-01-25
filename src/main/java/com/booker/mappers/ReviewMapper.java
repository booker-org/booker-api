package com.booker.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.booker.DTO.Review.CreateReviewDTO;
import com.booker.DTO.Review.ReviewDTO;
import com.booker.models.Book;
import com.booker.models.Review;
import com.booker.models.User;

@Component
public class ReviewMapper {
  @Autowired
  private UserMapper userMapper;

  @Autowired
  private BookMapper bookMapper;

  public ReviewDTO toDTO(Review review) {
    return new ReviewDTO(
      review.getId(),
      review.getScore(),
      review.getHeadline(),
      review.getText(),
      review.getLikeCount(),
      userMapper.toDTO(review.getUser()),
      bookMapper.toDTO(review.getBook()),
      review.getCreatedAt(),
      review.getUpdatedAt()
    );
  }

  public Review toEntity(CreateReviewDTO data, User user, Book book) {
    Review review = new Review();

    review.setScore(data.score());
    review.setHeadline(data.headline());
    review.setText(data.text());
    review.setUser(user);
    review.setBook(book);

    return review;
  }
}