package com.booker.exceptions;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice @Slf4j
public class GlobalExceptionHandler {
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
    ErrorResponse error = new ErrorResponse(
      HttpStatus.NOT_FOUND.value(),
      ex.getMessage(),
      LocalDateTime.now()
    );

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler(CoverException.class)
  public ResponseEntity<ErrorResponse> handleCoverException(CoverException ex) {
    ErrorResponse error = new ErrorResponse(
      HttpStatus.BAD_REQUEST.value(),
      ex.getMessage(),
      LocalDateTime.now()
    );

    return ResponseEntity.badRequest().body(error);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
    ErrorResponse error = new ErrorResponse(
      HttpStatus.BAD_REQUEST.value(),
      ex.getMessage(),
      LocalDateTime.now()
    );

    return ResponseEntity.badRequest().body(error);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
    log.warn("Authentication failed: Bad credentials");

    ErrorResponse error = new ErrorResponse(
      HttpStatus.UNAUTHORIZED.value(),
      "Invalid username or password",
      LocalDateTime.now()
    );

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
    log.warn("Authentication error: {}", ex.getMessage());

    ErrorResponse error = new ErrorResponse(
      HttpStatus.UNAUTHORIZED.value(),
      "Authentication failed",
      LocalDateTime.now()
    );

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
    log.warn("Access denied: {}", ex.getMessage());

    ErrorResponse error = new ErrorResponse(
      HttpStatus.FORBIDDEN.value(),
      "Access denied",
      LocalDateTime.now()
    );

    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
    log.warn("Type mismatch for parameter '{}': {}", ex.getName(), ex.getMessage());

    ErrorResponse error = new ErrorResponse(
      HttpStatus.BAD_REQUEST.value(),
      String.format("Invalid value for parameter '%s'", ex.getName()),
      LocalDateTime.now()
    );

    return ResponseEntity.badRequest().body(error);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();

    ex.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();

      errors.put(fieldName, errorMessage);
    });

    ValidationErrorResponse errorResponse = new ValidationErrorResponse(
      HttpStatus.BAD_REQUEST.value(),
      "Validation failed",
      LocalDateTime.now(),
      errors
    );

    return ResponseEntity.badRequest().body(errorResponse);
  }

  @ExceptionHandler(BusinessRuleException.class)
  public ResponseEntity<ErrorResponse> handleBusinessRule(BusinessRuleException ex) {
    HttpStatus status = HttpStatus.BAD_REQUEST;

    ErrorResponse error = new ErrorResponse(
      status.value(),
      ex.getMessage(),
      LocalDateTime.now()
    );

    return ResponseEntity.status(status).body(error);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception ex) {
    log.error("Unexpected error occurred", ex);

    ErrorResponse error = new ErrorResponse(
      HttpStatus.INTERNAL_SERVER_ERROR.value(),
      "An unexpected error occurred",
      LocalDateTime.now()
    );

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }

  // Error response classes
  record ErrorResponse(int status, String message, LocalDateTime timestamp) {}

  record ValidationErrorResponse(
    int status,
    String message,
    LocalDateTime timestamp,
    Map<String, String> errors
  ) {}
}