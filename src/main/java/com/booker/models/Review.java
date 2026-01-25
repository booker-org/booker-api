package com.booker.models;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name = "reviews")
@NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class Review extends BaseEntity {
  @Column(nullable = false)
  private BigDecimal score;

  @Column(length =  50)
  private String headline;

  @Column(nullable = false)
  private String text;

  @Column(nullable = false)
  private Integer likeCount;

  @ManyToOne @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne @JoinColumn(name = "book_id", nullable = false)
  private Book book;
}