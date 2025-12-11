package com.gym.backend.Shared.Exceptions;

import com.gym.backend.Comprobante.Domain.Exceptions.ComprobanteNotFoundException;
import com.gym.backend.Comprobante.Domain.Exceptions.ComprobanteDuplicadoException;
import com.gym.backend.Membresias.Domain.Exceptions.MembresiaNotFoundException;
import com.gym.backend.Membresias.Domain.Exceptions.MembresiaValidationException;
import com.gym.backend.Pago.Domain.Exceptions.PagoDuplicateException;
import com.gym.backend.Pago.Domain.Exceptions.PagoNotFoundException;
import com.gym.backend.Pago.Domain.Exceptions.PagoValidationException;
import com.gym.backend.PaymentCode.Domain.Exceptions.PaymentCodeNotFoundException;
import com.gym.backend.PaymentCode.Domain.Exceptions.PaymentCodeValidationException;
import com.gym.backend.Planes.Domain.Exceptions.PlanDuplicateException;
import com.gym.backend.Planes.Domain.Exceptions.PlanNotFoundException;
import com.gym.backend.Planes.Domain.Exceptions.PlanValidationException;
import com.gym.backend.Usuarios.Domain.Exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler global de excepciones para toda la aplicación.
 * Consolida el manejo de excepciones de todos los módulos.
 * 
 * Principios aplicados:
 * - Single Responsibility: Solo maneja excepciones
 * - DRY: Elimina código duplicado de ExceptionHandlers individuales
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ==================== MÉTODO BASE REUTILIZABLE ====================

    /**
     * Construye una respuesta de error estandarizada
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            String message, String code, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("message", message);
        errorResponse.put("code", code);
        errorResponse.put("status", status.value());
        return new ResponseEntity<>(errorResponse, status);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            String message, HttpStatus status) {
        return buildErrorResponse(message, status.name(), status);
    }

    // ==================== EXCEPCIONES DE VALIDACIÓN ====================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("code", "VALIDATION_ERROR");
        response.put("error", "Validation Failed");
        response.put("errors", errors);

        log.warn("Validation error: {}", errors);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), "VALIDATION_ERROR", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex) {
        log.warn("Illegal state: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), "BUSINESS_RULE_VIOLATION", HttpStatus.BAD_REQUEST);
    }

    // ==================== EXCEPCIONES DE USUARIOS ====================

    @ExceptionHandler(UsuarioNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUsuarioNotFound(UsuarioNotFoundException ex) {
        log.warn("Usuario not found: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), ex.getCode(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsuarioDuplicateException.class)
    public ResponseEntity<Map<String, Object>> handleUsuarioDuplicate(UsuarioDuplicateException ex) {
        log.warn("Usuario duplicate: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), ex.getCode(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UsuarioValidationException.class)
    public ResponseEntity<Map<String, Object>> handleUsuarioValidation(UsuarioValidationException ex) {
        log.warn("Usuario validation: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), ex.getCode(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsuarioInactiveException.class)
    public ResponseEntity<Map<String, Object>> handleUsuarioInactive(UsuarioInactiveException ex) {
        log.warn("Usuario inactive: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), ex.getCode(), HttpStatus.FORBIDDEN);
    }

    // ==================== EXCEPCIONES DE PLANES ====================

    @ExceptionHandler(PlanNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePlanNotFound(PlanNotFoundException ex) {
        log.warn("Plan not found: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), ex.getCode(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PlanDuplicateException.class)
    public ResponseEntity<Map<String, Object>> handlePlanDuplicate(PlanDuplicateException ex) {
        log.warn("Plan duplicate: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), ex.getCode(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PlanValidationException.class)
    public ResponseEntity<Map<String, Object>> handlePlanValidation(PlanValidationException ex) {
        log.warn("Plan validation: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), ex.getCode(), HttpStatus.BAD_REQUEST);
    }

    // ==================== EXCEPCIONES DE PAGOS ====================

    @ExceptionHandler(PagoNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePagoNotFound(PagoNotFoundException ex) {
        log.warn("Pago not found: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), ex.getCode(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PagoDuplicateException.class)
    public ResponseEntity<Map<String, Object>> handlePagoDuplicate(PagoDuplicateException ex) {
        log.warn("Pago duplicate: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), ex.getCode(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PagoValidationException.class)
    public ResponseEntity<Map<String, Object>> handlePagoValidation(PagoValidationException ex) {
        log.warn("Pago validation: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), ex.getCode(), HttpStatus.BAD_REQUEST);
    }

    // ==================== EXCEPCIONES DE MEMBRESÍAS ====================

    @ExceptionHandler(MembresiaNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleMembresiaNotFound(MembresiaNotFoundException ex) {
        log.warn("Membresia not found: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), "MEMBRESIA_NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MembresiaValidationException.class)
    public ResponseEntity<Map<String, Object>> handleMembresiaValidation(MembresiaValidationException ex) {
        log.warn("Membresia validation: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), "MEMBRESIA_VALIDATION_ERROR", HttpStatus.BAD_REQUEST);
    }

    // ==================== EXCEPCIONES DE PAYMENT CODE ====================

    @ExceptionHandler(PaymentCodeNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePaymentCodeNotFound(PaymentCodeNotFoundException ex) {
        log.warn("PaymentCode not found: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), "PAYMENT_CODE_NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PaymentCodeValidationException.class)
    public ResponseEntity<Map<String, Object>> handlePaymentCodeValidation(PaymentCodeValidationException ex) {
        log.warn("PaymentCode validation: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), "PAYMENT_CODE_VALIDATION_ERROR", HttpStatus.BAD_REQUEST);
    }

    // ==================== EXCEPCIONES DE COMPROBANTES ====================

    @ExceptionHandler(ComprobanteNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleComprobanteNotFound(ComprobanteNotFoundException ex) {
        log.warn("Comprobante not found: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), "COMPROBANTE_NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ComprobanteDuplicadoException.class)
    public ResponseEntity<Map<String, Object>> handleComprobanteDuplicado(ComprobanteDuplicadoException ex) {
        log.warn("Comprobante duplicado: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), "COMPROBANTE_DUPLICADO", HttpStatus.CONFLICT);
    }

    // ==================== EXCEPCIONES GENÉRICAS ====================

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime exception: {}", ex.getMessage(), ex);
        return buildErrorResponse(
                "Error interno del servidor: " + ex.getMessage(),
                "INTERNAL_ERROR",
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Unexpected exception: {}", ex.getMessage(), ex);
        return buildErrorResponse(
                "Error inesperado. Por favor, contacte al administrador.",
                "UNEXPECTED_ERROR",
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
