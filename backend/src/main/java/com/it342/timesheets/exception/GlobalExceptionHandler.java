package com.it342.timesheets.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        String message = ex.getMessage();
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if ("Invalid credentials".equals(message) || "User not found".equals(message) || "Invalid or expired token".equals(message)) {
            status = HttpStatus.UNAUTHORIZED;
        } else if ("User already exists".equals(message) || "Email already exists".equals(message)) {
            status = HttpStatus.CONFLICT;
        } else if ("Only employees can clock in/out".equals(message)) {
            status = HttpStatus.FORBIDDEN;
        } else if ("Account is disabled".equals(message) || "Account locked".equals(message)) {
            status = HttpStatus.FORBIDDEN;
        }
        return ResponseEntity.status(status).body(Map.of("error", message));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        String firstError = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("Validation failed");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", firstError));
    }
}
