package com.booker.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.booker.models.Genre;

public interface GenreRepository extends JpaRepository<Genre, UUID> {
  Optional<Genre> findByName(String name);
}