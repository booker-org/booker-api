package com.booker.exceptions;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
  private final int status;
  private final String code;
  private final String message;
  private final LocalDateTime timestamp;
  private final Map<String, Object> meta;

  public ErrorResponse(int status, String code, String message, LocalDateTime timestamp) {
    this(status, code, message, timestamp, null);
  }
}
