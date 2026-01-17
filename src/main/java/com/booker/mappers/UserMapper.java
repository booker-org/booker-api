package com.booker.mappers;

import com.booker.DTO.User.UserDTO;
import com.booker.models.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
  
  public UserDTO toDTO(User user) {
    if (user == null) {
      return null;
    }
    
    return new UserDTO(
      user.getId(),
      user.getName(),
      user.getUsername(),
      user.getEmail(),
      user.getBio(),
      user.getCreatedAt(),
      user.getUpdatedAt()
    );
  }
}
