package com.booker.repositories;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.booker.models.Review;

public interface ReviewRepository extends JpaRepository<Review, UUID> {}