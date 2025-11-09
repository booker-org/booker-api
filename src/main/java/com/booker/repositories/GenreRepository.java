package com.booker.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.booker.models.Genre;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
  Optional<Genre> findByName(String name);
}