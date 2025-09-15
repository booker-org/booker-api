package com.booker.entities;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "genres")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({ "id", "name", "books", "createdAt", "updatedAt" })
public class Genre extends BaseEntity {

  @Column(nullable = false, unique = true)
  private String name;

  @ManyToMany(mappedBy = "genres", fetch = FetchType.LAZY)
  private Set<Book> books;
}
