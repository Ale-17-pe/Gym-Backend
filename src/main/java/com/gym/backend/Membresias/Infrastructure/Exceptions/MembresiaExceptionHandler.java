package com.gym.backend.Membresias.Infrastructure.Exceptions;

import com.gym.backend.Membresias.Domain.Exceptions.MembresiaNotFoundException;
import com.gym.backend.Membresias.Domain.Exceptions.MembresiaValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class MembresiaExceptionHandler {

    @ExceptionHandler(MembresiaNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleMembresiaNotFound(MembresiaNotFoundException ex) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MembresiaValidationException.class)
    public ResponseEntity<Map<String, Object>> handleMembresiaValidation(MembresiaValidationException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(Exception ex, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("status", status.value());

        return new ResponseEntity<>(errorResponse, status);
    }
}