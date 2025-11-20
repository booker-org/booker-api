package com.booker.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.booker.DTO.User.CreateUserDTO;
import com.booker.DTO.User.UpdateUserDTO;
import com.booker.exceptions.ResourceNotFoundException;
import com.booker.models.User;
import com.booker.repositories.UserRepository;

@Service
public class UserService {
  @Autowired
  private UserRepository repository;

  @Transactional(readOnly = true)
  public Page<User> findAll(Pageable pageable) { return repository.findAll(pageable); }

  @Transactional(readOnly = true)
  public User findById(UUID id) {
    return repository
      .findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado para o ID: " + id))
    ;
  }

  @Transactional
  public User save(CreateUserDTO data) {
    User user = new User(data);

    return repository.save(user);
  }

  @Transactional
  public void update(UUID id, UpdateUserDTO data) {
    User user = findById(id);

    user.setName(data.name());
    user.setUsername(data.username());
    user.setEmail(data.email());
    user.setPassword(data.password());
    user.setBio(data.bio());

    repository.save(user);
  }

  @Transactional
  public void delete(UUID id) { repository.deleteById(id); }
}