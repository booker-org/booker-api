package com.booker.controllers;

import java.net.URI;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.booker.models.User;
import com.booker.services.ReviewService;

import static com.booker.constants.Auth.ADMIN_AUTHORIZATION;
import static com.booker.constants.Auth.ADMIN_ROLE;
import static com.booker.constants.Auth.REVIEW_OWNER_OR_ADMIN;

@RestController @RequestMapping("/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Review management endpoints")
public class ReviewController {
  private final ReviewService service;
  private final ReviewMapper mapper;

  @GetMapping @PreAuthorize(ADMIN_AUTHORIZATION)
  @Operation(summary = "Get all reviews - " + ADMIN_ROLE, description = "Get paginated list of all reviews")
  @ApiResponses(@ApiResponse(responseCode = "200", description = "Review list successfully retrieved"))
  public ResponseEntity<Page<ReviewDTO>> getAll(
    @ParameterObject @PageableDefault(size = 10)
    Pageable pageable
  ) {
    Page<Review> reviews = service.findAll(pageable);
    Page<ReviewDTO> result = reviews.map(mapper::toDTO);

    return ResponseEntity.ok(result);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get review by ID", description = "Get a specific review by its ID")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Review found"),
    @ApiResponse(responseCode = "404", description = "Review not found")
  })
  public ResponseEntity<ReviewDTO> getById(@PathVariable UUID id) {
    Review review = service.findById(id);
    ReviewDTO result = mapper.toDTO(review);

    return ResponseEntity.ok(result);
  }

  @PostMapping
  @Operation(summary = "Create new review", description = "Create a new review for the authenticated user")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Review created successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid review data"),
    @ApiResponse(responseCode = "409", description = "User already reviewed this book", content = @Content)
  })
  public ResponseEntity<ReviewDTO> create(
    @AuthenticationPrincipal User currentUser,
    @Valid @RequestBody CreateReviewDTO data
  ) {
    Review review = service.create(data, currentUser);
    ReviewDTO result = mapper.toDTO(review);
    URI uri = URI.create("/reviews/" + review.getId());

    return ResponseEntity.created(uri).body(result);
  }

  @PatchMapping("/{id}") @PreAuthorize(REVIEW_OWNER_OR_ADMIN)
  @Operation(summary = "Update review", description = "Update an existing review (owner or admin only)")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "Review updated successfully"),
    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
    @ApiResponse(responseCode = "404", description = "Review not found", content = @Content),
    @ApiResponse(responseCode = "400", description = "Invalid review data", content = @Content)
  })
  public ResponseEntity<Void> patch(
    @PathVariable UUID id,
    @Valid @RequestBody UpdateReviewDTO data
  ) {
    service.update(id, data);

    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}") @PreAuthorize(REVIEW_OWNER_OR_ADMIN)
  @Operation(summary = "Delete review", description = "Delete a review by ID (owner or admin only)")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "Review deleted successfully"),
    @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
    @ApiResponse(responseCode = "404", description = "Review not found")
  })
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    service.delete(id);

    return ResponseEntity.noContent().build();
  }
}