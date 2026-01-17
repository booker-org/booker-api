package com.booker.services;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.booker.DTO.Auth.RegisterRequestDTO;
import com.booker.DTO.User.CreateUserDTO;
import com.booker.DTO.User.UpdatePasswordDTO;
import com.booker.DTO.User.UpdateUserDTO;
import com.booker.exceptions.ResourceNotFoundException;
import com.booker.models.User;
import com.booker.models.enums.Role;
import com.booker.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
  private final UserRepository repository;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return repository.findByUsername(username)
      .or(() -> repository.findByEmail(username))
      .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com username ou email: " + username));
  }

  @Transactional(readOnly = true)
  public Page<User> findAll(Pageable pageable) {
    return repository.findAll(pageable);
  }

  @Transactional(readOnly = true)
  public User findById(UUID id) {
    return repository
      .findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado para o ID: " + id));
  }

  @Transactional(readOnly = true)
  public User findByUsername(String username) {
    return repository
      .findByUsername(username)
      .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado para o username: " + username));
  }

  @Transactional(readOnly = true)
  public User findByEmail(String email) {
    return repository
      .findByEmail(email)
      .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado para o email: " + email));
  }

  @Transactional
  public User save(RegisterRequestDTO data) {
    if (repository.existsByUsername(data.username()))
      throw new IllegalArgumentException("Esse nome de usuário já está em uso");

    if (repository.existsByEmail(data.email()))
      throw new IllegalArgumentException("Esse email já está em uso");

    User user = new User();
    user.setName(data.name());
    user.setUsername(data.username());
    user.setEmail(data.email());
    user.setPassword(passwordEncoder.encode(data.password()));
    user.setRole(Role.USER);
    user.setEnabled(true);
    user.setAccountNonLocked(true);

    return repository.save(user);
  }

  @Transactional
  public User save(CreateUserDTO data) {
    if (repository.existsByUsername(data.username()))
      throw new IllegalArgumentException("Esse nome de usuário já está em uso");

    if (repository.existsByEmail(data.email()))
      throw new IllegalArgumentException("Esse email já está em uso");

    User user = new User();
    user.setName(data.name());
    user.setUsername(data.username());
    user.setEmail(data.email());
    user.setPassword(passwordEncoder.encode(data.password()));
    user.setBio(data.bio());
    user.setRole(data.role());
    user.setEnabled(data.enabled());
    user.setAccountNonLocked(data.accountNonLocked());

    return repository.save(user);
  }

  @Transactional
  public void update(UUID id, UpdateUserDTO data) {
    User user = findById(id);

    if (data.username() != null) {
      if (repository.existsByUsername(data.username()))
        throw new IllegalArgumentException("Esse nome de usuário já está em uso");
      user.setUsername(data.username());
    }

    if (data.email() != null) {
      if (repository.existsByEmail(data.email()))
        throw new IllegalArgumentException("Esse email já está em uso");
      user.setEmail(data.email());
    }

    if (data.name() != null)
      user.setName(data.name());
    if (data.bio() != null)
      user.setBio(data.bio());

    repository.save(user);
  }

  @Transactional
  public void updatePassword(UUID id, UpdatePasswordDTO data) {
    User user = findById(id);

    if (!passwordEncoder.matches(data.currentPassword(), user.getPassword())) {
      throw new IllegalArgumentException("Senha incorreta");
    }

    user.setPassword(passwordEncoder.encode(data.newPassword()));

    repository.save(user);
  }

  @Transactional
  public void delete(UUID id) {
    repository.deleteById(id);
  }
}