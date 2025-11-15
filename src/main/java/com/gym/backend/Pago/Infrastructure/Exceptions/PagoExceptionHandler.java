package com.gym.backend.Pago.Infrastructure.Exceptions;

import com.gym.backend.Pago.Domain.Exceptions.PagoNotFoundException;
import com.gym.backend.Pago.Domain.Exceptions.PagoValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class PagoExceptionHandler {

    @ExceptionHandler(PagoNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePagoNotFound(PagoNotFoundException ex) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PagoValidationException.class)
    public ResponseEntity<Map<String, Object>> handlePagoValidation(PagoValidationException ex) {
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