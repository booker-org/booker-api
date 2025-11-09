package com.booker.repositories;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.booker.models.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {
  Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);

  Page<Book> findByAuthorId(UUID authorId, Pageable pageable);

  // Customized search with JPQL
  @Query("SELECT b FROM Book b WHERE b.title LIKE %:title% OR b.synopsis LIKE %:title%")
  Page<Book> findByTitleOrSynopsisContaining(@Param("title") String title, Pageable pageable);
}