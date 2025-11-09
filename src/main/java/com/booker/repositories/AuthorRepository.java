package com.booker.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.booker.models.Author;

@Repository
public interface AuthorRepository extends JpaRepository<Author, UUID> {
  Optional<Author> findByName(String name);
}