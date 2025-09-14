package com.booker.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.booker.entities.Genre;

import java.util.Optional;


@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

    Optional<Genre> findById(Long id);
    Optional<Genre> findByName(String name);

}
