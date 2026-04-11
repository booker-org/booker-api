package com.booker.exceptions;

import lombok.Getter;

@Getter
public class InvalidTokenException extends BusinessRuleException {
  public InvalidTokenException(String message) {
    super(message, ErrorCode.INVALID_TOKEN);
  }

  public InvalidTokenException(String message, ErrorCode code) {
    super(message, code);
  }
}
