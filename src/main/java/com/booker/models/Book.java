package com.booker.models;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
  "id", "title", "synopsis", "pageCount", "author", "genres", "coverUrl", "createdAt", "updatedAt"
})
public class Book extends BaseEntity {
  @Column(length = 200, nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String synopsis;

  @Column(name = "page_count", nullable = false)
  private Integer pageCount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", nullable = false)
  private Author author;

  @Column(length = 2048, name = "cover_url")
  private String coverUrl;

  @ManyToMany(fetch = FetchType.LAZY, cascade = {
    CascadeType.PERSIST, CascadeType.MERGE
  })
  @JoinTable(name = "book_genres", joinColumns = @JoinColumn(name = "book_id"),
    inverseJoinColumns = @JoinColumn(name = "genre_id"))
  private Set<Genre> genres = new HashSet<>();
}