package com.booker.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.booker.models.Genre;

@Repository
public interface GenreRepository extends JpaRepository<Genre, UUID> {
  Optional<Genre> findByName(String name);
}