package com.booker.controllers;

import java.net.URI;
import java.util.List;
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

import com.booker.DTO.User.CreateUserDTO;
import com.booker.DTO.User.UpdatePasswordDTO;
import com.booker.DTO.User.UpdateUserDTO;
import com.booker.DTO.User.UserDTO;
import com.booker.models.User;
import com.booker.services.UserService;

@RestController @RequestMapping("/users")
public class UserController {
  @Autowired
  private UserService service;

  @GetMapping
  public ResponseEntity<List<UserDTO>> getAll(@ParameterObject @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
    Page<User> users = service.findAll(pageable);

    List<UserDTO> usersDTO = users.stream().map(UserDTO::new).toList();

    return ResponseEntity.ok(usersDTO);
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserDTO> getById(@PathVariable UUID id) {
    User user = service.findById(id);

    return ResponseEntity.ok(new UserDTO(user));
  }

  @PostMapping
  public ResponseEntity<UserDTO> post(@RequestBody @Valid CreateUserDTO data) {
    User savedUser = service.save(data);

    URI uri = URI.create("/users/" + savedUser.getId());

    return ResponseEntity.created(uri).body(new UserDTO(savedUser));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<Void> put(
    @PathVariable UUID id,
    @RequestBody @Valid UpdateUserDTO data
  ) {
    service.update(id, data);

    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{id}/password")
  public ResponseEntity<Void> updatePassword(
    @PathVariable UUID id,
    @RequestBody @Valid UpdatePasswordDTO data
  ) {
    service.updatePassword(id, data.currentPassword(), data.newPassword());

    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    service.delete(id);

    return ResponseEntity.noContent().build();
  }
}