package com.booker.controllers;

import java.net.URI;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.booker.DTO.Review.CreateReviewDTO;
import com.booker.DTO.Review.ReviewDTO;
import com.booker.DTO.Review.UpdateReviewDTO;
import com.booker.mappers.ReviewMapper;
import com.booker.models.Review;
import com.booker.services.ReviewService;

@RestController @RequestMapping("/reviews")
public class ReviewController {
  @Autowired
  private ReviewService service;

  @Autowired
  private ReviewMapper mapper;

  @GetMapping
  public ResponseEntity<Page<ReviewDTO>> getAll(
    @ParameterObject @PageableDefault(size = 10)
    Pageable pageable
  ) {
    Page<Review> reviews = service.findAll(pageable);
    Page<ReviewDTO> result = reviews.map(mapper::toDTO);

    return ResponseEntity.ok(result);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ReviewDTO> getById(@PathVariable UUID id) {
    Review review = service.findById(id);
    ReviewDTO result = mapper.toDTO(review);

    return ResponseEntity.ok(result);
  }

  @PostMapping
  public ResponseEntity<ReviewDTO> create(@Valid @RequestBody CreateReviewDTO data) {
    Review review = service.create(data);
    ReviewDTO result = mapper.toDTO(review);
    URI uri = URI.create("/reviews/" + review.getId());

    return ResponseEntity.created(uri).body(result);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<Void> patch(
    @PathVariable UUID id,
    @Valid @RequestBody UpdateReviewDTO data
  ) {
    service.update(id, data);

    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    service.delete(id);

    return ResponseEntity.noContent().build();
  }
}