package com.booker.models;

import com.booker.DTO.User.CreateUserDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name = "users")
@NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class User extends BaseEntity {
  @Column(length = 100, nullable = false)
  private String name;

  @Column(length = 30, nullable = false, unique = true)
  private String username;

  @Column(length = 254, nullable = false, unique = true)
  private String email;

  @Column(length = 255, nullable = false)
  private String password;
  
  @Column(length = 300)
  private String bio;

  public User(CreateUserDTO data) {
    setName(data.name());
    setUsername(data.username());
    setEmail(data.email());
    setPassword(data.password());
    setBio(data.bio());
  }
}