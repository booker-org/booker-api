package com.booker.DTO.Review;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.booker.DTO.User.UserDTO;

public record SimpleReviewDTO(
  UUID id,
  BigDecimal score,
  String headline,
  String text,
  Integer likeCount,
  UserDTO user,
  LocalDateTime createdAt,
  LocalDateTime updatedAt
) {}