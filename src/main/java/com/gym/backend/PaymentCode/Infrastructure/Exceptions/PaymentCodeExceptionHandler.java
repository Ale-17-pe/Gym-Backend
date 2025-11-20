package com.gym.backend.PaymentCode.Infrastructure.Exceptions;

import com.gym.backend.PaymentCode.Domain.Exceptions.PaymentCodeNotFoundException;
import com.gym.backend.PaymentCode.Domain.Exceptions.PaymentCodeValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class PaymentCodeExceptionHandler {

    @ExceptionHandler(PaymentCodeNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePaymentCodeNotFound(PaymentCodeNotFoundException ex) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PaymentCodeValidationException.class)
    public ResponseEntity<Map<String, Object>> handlePaymentCodeValidation(PaymentCodeValidationException ex) {
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