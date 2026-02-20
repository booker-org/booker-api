package com.booker.exceptions;

import lombok.Getter;

@Getter
public class BusinessRuleException extends RuntimeException {
  private final ErrorCode code;

  public BusinessRuleException(String message) {
    super(message);
    this.code = ErrorCode.BUSINESS_RULE_VIOLATION;
  }

  public BusinessRuleException(String message, ErrorCode code) {
    super(message);
    this.code = code;
  }
}
