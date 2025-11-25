package com.gym.backend.Auth.Infrastructure;

import com.gym.backend.Auth.Domain.*;
import com.gym.backend.Usuarios.Domain.Exceptions.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUseCase useCase;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginCommand command) {
        try {
            AuthResponse response = useCase.login(command);
            return ResponseEntity.ok(response);
        } catch (UsuarioNotFoundException | UsuarioValidationException e) {
            log.warn("Login fallido para: {} - {}", command.emailOrDni(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Credenciales inválidas"));
        } catch (UsuarioInactiveException e) {
            log.warn("Usuario inactivo intentó login: {}", command.emailOrDni());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("Usuario desactivado"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterCommand command) {
        try {
            AuthResponse response = useCase.registrar(command);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (UsuarioDuplicateException e) {
            log.warn("Registro fallido - duplicado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (UsuarioValidationException e) {
            log.warn("Registro fallido - validación: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error en registro: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno del servidor"));
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Token requerido"));
            }
            String token = authHeader.substring(7);
            useCase.validarToken(token);
            return ResponseEntity.ok().build();
        } catch (UsuarioValidationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Token inválido"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok().body(new MessageResponse("Refresh token no implementado"));
    }

    public record ErrorResponse(String message) {
    }

    public record MessageResponse(String message) {
    }
}