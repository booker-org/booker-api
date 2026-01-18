package com.booker.models;

import java.util.Collections;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name = "genres")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@JsonPropertyOrder({ "id", "name", "books", "createdAt", "updatedAt" })
public class Genre extends BaseEntity {
  @Column(length = 100, nullable = false, unique = true)
  private String name;

  @ManyToMany(mappedBy = "genres")
  private Set<Book> books;

  public Set<Book> getBooks() {
    return books != null ? Collections.unmodifiableSet(books) : Collections.emptySet();
  }

  public void setBooks(Set<Book> books) {
    this.books = books;
  }
}