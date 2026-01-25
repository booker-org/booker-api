package com.booker.controllers;

import java.net.URI;
import java.util.UUID;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.booker.DTO.User.CreateUserDTO;
import com.booker.DTO.User.UpdatePasswordDTO;
import com.booker.DTO.User.UpdateUserDTO;
import com.booker.DTO.User.UserDTO;
import com.booker.mappers.UserMapper;
import com.booker.models.User;
import com.booker.services.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management endpoints")
public class UserController {
  private final UserService service;
  private final UserMapper userMapper;

  @GetMapping
  @Operation(summary = "Get all users", description = "Get paginated list of all users (max 100 per page)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User list successfully retrieved")
  })
  public ResponseEntity<Page<UserDTO>> getAll(
      @ParameterObject @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
    Page<User> users = service.findAll(pageable);

    return ResponseEntity.ok(users.map(userMapper::toDTO));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get user by ID", description = "Get a specific user by its ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User found"),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  public ResponseEntity<UserDTO> getById(@PathVariable UUID id) {
    User user = service.findById(id);

    return ResponseEntity.ok(userMapper.toDTO(user));
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Create new user (Admin)", description = "Create a new user with full control over all fields (Admin only)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "User created successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid user data"),
      @ApiResponse(responseCode = "403", description = "Access denied - Administrator profile required")
  })
  public ResponseEntity<UserDTO> post(@RequestBody @Valid CreateUserDTO data) {
    User savedUser = service.save(data);

    URI uri = URI.create("/users/" + savedUser.getId());

    return ResponseEntity.created(uri).body(userMapper.toDTO(savedUser));
  }

  @PatchMapping("/{id}")
  @Operation(summary = "Update user", description = "Update an existing user's information")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User updated successfully"),
      @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
      @ApiResponse(responseCode = "400", description = "Invalid user data", content = @Content)
  })
  public ResponseEntity<Void> patch(
      @PathVariable UUID id,
      @RequestBody @Valid UpdateUserDTO data) {
    service.update(id, data);

    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{id}/password")
  @Operation(summary = "Update user password", description = "Update an existing user's password")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Password updated successfully"),
      @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
      @ApiResponse(responseCode = "400", description = "Invalid password data", content = @Content)
  })
  public ResponseEntity<Void> updatePassword(
      @PathVariable UUID id,
      @RequestBody @Valid UpdatePasswordDTO data) {
    service.updatePassword(id, data);

    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete user", description = "Delete a user by ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "User deleted successfully"),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    service.delete(id);

    return ResponseEntity.noContent().build();
  }
}