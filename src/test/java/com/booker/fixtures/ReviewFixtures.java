package com.booker.fixtures;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.booker.DTO.Review.CreateReviewDTO;
import com.booker.models.Review;

import static com.booker.constants.Tests.DEFAULT_TEST_ID;

public final class ReviewFixtures {
  private ReviewFixtures() {}

  public static CreateReviewDTO validCreateReviewDTO() {
    return new CreateReviewDTO(
      BigDecimal.ONE,
      "Headline",
      "Text",
      DEFAULT_TEST_ID
    );
  }

  public static Review validReview() {
    Review review = new Review();

    review.setId(DEFAULT_TEST_ID);
    review.setScore(BigDecimal.ONE);
    review.setHeadline("Headline");
    review.setText("Text");
    review.setUser(UserFixtures.validUser());
    review.setBook(BookFixtures.validBook());
    review.setCreatedAt(LocalDateTime.now());
    review.setUpdatedAt(LocalDateTime.now());

    return review;
  }
}