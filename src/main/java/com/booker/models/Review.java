package com.booker.models;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name = "reviews")
@NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class Review extends BaseEntity {
  @DecimalMin(value = "0.0", message = "Score must be at least {value}")
  @DecimalMax(value = "5.0", message = "Score must be at most {value}")
  @Column(nullable = false, precision = 2, scale = 1)
  private BigDecimal score;

  @Column(length =  50)
  private String headline;

  @Column(nullable = false)
  private String text;

  @Column(nullable = false)
  private Integer likeCount = 0;

  @ManyToOne @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne @JoinColumn(name = "book_id", nullable = false)
  private Book book;
}