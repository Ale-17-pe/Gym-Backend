package com.gym.backend.Planes.Infrastructure.Exceptions;

import com.gym.backend.Planes.Domain.Exceptions.PlanDuplicateException;
import com.gym.backend.Planes.Domain.Exceptions.PlanException;
import com.gym.backend.Planes.Domain.Exceptions.PlanNotFoundException;
import com.gym.backend.Planes.Domain.Exceptions.PlanValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class PlanExceptionHandler {

    @ExceptionHandler(PlanNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePlanNotFound(PlanNotFoundException ex) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PlanDuplicateException.class)
    public ResponseEntity<Map<String, Object>> handlePlanDuplicate(PlanDuplicateException ex) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PlanValidationException.class)
    public ResponseEntity<Map<String, Object>> handlePlanValidation(PlanValidationException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(PlanException ex, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("code", ex.getCode());
        errorResponse.put("status", status.value());

        return new ResponseEntity<>(errorResponse, status);
    }
}
