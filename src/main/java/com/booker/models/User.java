package com.booker.models;

import com.booker.config.security.SecurityConstants;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User extends BaseEntity implements UserDetails {
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
  
  @Enumerated(EnumType.STRING)
  @Column(length = 20, nullable = false)
  private Role role = Role.USER;

  @Column(nullable = false)
  private Boolean enabled = true;

  @Column(name = "account_non_locked", nullable = false)
  private Boolean accountNonLocked = true;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(SecurityConstants.ROLE_PREFIX + role.name()));
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return accountNonLocked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }
}