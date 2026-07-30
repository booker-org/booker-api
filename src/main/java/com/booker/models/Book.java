package com.booker.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Entity @Table(name = "books")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@JsonPropertyOrder({
  "id",
  "title",
  "synopsis",
  "pageCount",
  "releaseYear",
  "author",
  "genres",
  "coverUrl",
  "rating",
  "ratingsCount",
  "createdAt",
  "updatedAt"
})
public class Book extends BaseEntity {
  @Column(length = 200, nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String synopsis;

  @Column(name = "page_count", nullable = false)
  private Integer pageCount;

  @ManyToOne @JoinColumn(name = "author_id", nullable = false)
  private Author author;

  @Column(length = 2048, name = "cover_url")
  private String coverUrl;

  @Column(name = "release_year", nullable = false)
  private Short releaseYear;

  @Column(precision = 2, scale = 1)
  private BigDecimal rating = BigDecimal.ZERO;

  @Column(name = "ratings_count")
  private Integer ratingsCount = 0;

  @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
  @JoinTable(
    name = "book_genres",
    joinColumns = @JoinColumn(name = "book_id"),
    inverseJoinColumns = @JoinColumn(name = "genre_id")
  )
  private Set<Genre> genres = new HashSet<>();

  @OneToMany(
    mappedBy = "book",
    cascade = CascadeType.ALL,
    orphanRemoval = true
  )
  private List<Review> reviews = new ArrayList<>();

  public Set<Genre> getGenres() {
    return genres != null ? Collections.unmodifiableSet(genres) : Collections.emptySet();
  }

  public void setGenres(Set<Genre> genres) {
    this.genres = genres;
  }
}