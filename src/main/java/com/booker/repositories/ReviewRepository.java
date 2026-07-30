package com.booker.repositories;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.booker.models.Review;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
  Page<Review> findByBookId(UUID bookID, Pageable pageable);

  @Query("SELECT AVG(r.score) FROM Review r WHERE r.book.id = :bookID")
  BigDecimal findAverageScoreByBookId(UUID bookID);

  long countByBookId(UUID bookID);
}