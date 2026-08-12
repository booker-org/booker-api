package com.booker.repositories;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.booker.models.Review;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
  Page<Review> findByBookId(UUID bookID, Pageable pageable);
}