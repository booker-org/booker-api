package com.booker.DTO.Review;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.booker.DTO.Book.BookDTO;
import com.booker.DTO.User.UserDTO;

public record ReviewDTO(
  UUID id,
  BigDecimal score,
  String headline,
  String text,
  Integer likeCount,
  UserDTO user,
  BookDTO book,
  LocalDateTime createdAt,
  LocalDateTime updatedAt
) {}