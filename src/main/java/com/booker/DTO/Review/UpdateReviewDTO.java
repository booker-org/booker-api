package com.booker.DTO.Review;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record UpdateReviewDTO(
  @NotNull(message = "Score is required")
  @PositiveOrZero(message = "Score must be a positive number")
  @Max(value = 5, message = "Score max value is {max}")
  @Digits(integer = 1, fraction = 1, message = "Score must have up to {integer} integer digit and {fraction} decimal digit")
  BigDecimal score,

  @Size(min =  1, max = 50, message = "Headline length must be between {min} and {max}")
  String headline,

  @NotBlank(message = "Text is required")
  @Size(max = 2048, message = "Text length must be at most {max}")
  String text
) {}