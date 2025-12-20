package com.gym.backend.Auth.Infrastructure;

import com.gym.backend.Auth.Domain.*;
import com.gym.backend.Shared.Security.JwtService;
import com.gym.backend.Shared.TwoFactorAuth.TwoFactorAuthService;
import com.gym.backend.Usuarios.Domain.Exceptions.*;
import com.gym.backend.Usuarios.Domain.Usuario;
import com.gym.backend.Usuarios.Domain.UsuarioUseCase;
import com.gym.backend.Usuarios.Domain.UsuarioRepositoryPort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUseCase useCase;
    private final TwoFactorAuthService twoFactorAuthService;
    private final JwtService jwtService;
    private final UsuarioUseCase usuarioUseCase;
    private final UsuarioRepositoryPort usuarioRepository;
    private final com.gym.backend.Shared.PasswordReset.PasswordResetService passwordResetService;
    private final PasswordEncoder passwordEncoder;
    private final com.gym.backend.Shared.Email.EmailVerificationService emailVerificationService;
    private final com.gym.backend.Fidelidad.Application.PuntosFidelidadUseCase puntosFidelidadUseCase;

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

    @PostMapping("/verify-2fa")
    public ResponseEntity<?> verify2FA(@Valid @RequestBody Verify2FARequest request) {
        try {
            boolean isValid = twoFactorAuthService.validateCode(request.email(), request.code());

            if (!isValid) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Código inválido o expirado"));
            }

            Usuario usuario = usuarioUseCase.obtenerPorEmail(request.email());
            String token = jwtService.generateToken(usuario);
            LocalDateTime expiracion = jwtService.getExpirationFromToken(token);

            AuthResponse response = new AuthResponse(
                    token,
                    "Bearer",
                    usuario.getId(),
                    usuario.getNombreCompleto(),
                    usuario.getEmail(),
                    usuario.getDni(),
                    usuario.getRol().name(),
                    usuario.getPersona() != null && usuario.getPersona().getGenero() != null
                            ? usuario.getPersona().getGenero().name()
                            : "PREFIERO_NO_DECIR",
                    usuario.getActivo(),
                    usuario.getPersona() != null ? usuario.getPersona().getFotoPerfilUrl() : null,
                    expiracion,
                    LocalDateTime.now(),
                    false,
                    "Autenticación completada");

            log.info("✅ 2FA verificado exitosamente para: {}", request.email());
            return ResponseEntity.ok(response);

        } catch (UsuarioNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Usuario no encontrado"));
        } catch (Exception e) {
            log.error("Error en verificación 2FA: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error en verificación"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterCommand command) {
        try {
            AuthResponse response = useCase.registrar(command);

            // Enviar código de verificación de email automáticamente
            try {
                emailVerificationService.sendVerificationCode(command.email());
                log.info("📧 Código de verificación enviado a: {}", command.email());
            } catch (Exception emailError) {
                log.warn("⚠️ No se pudo enviar email de verificación: {}", emailError.getMessage());
            }

            // Otorgar puntos de fidelidad por registro
            try {
                puntosFidelidadUseCase.otorgarPuntos(
                        response.usuarioId(),
                        com.gym.backend.Fidelidad.Domain.Enum.MotivoGanancia.REGISTRO,
                        response.usuarioId(),
                        "REGISTRO");
                log.info("🎯 Puntos de bienvenida otorgados a usuario: {}", response.usuarioId());
            } catch (Exception puntosError) {
                log.warn("⚠️ No se pudieron otorgar puntos de registro: {}", puntosError.getMessage());
            }

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

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            usuarioUseCase.obtenerPorEmail(request.email());
            passwordResetService.generateResetCode(request.email());

            log.info("Código de recuperación enviado a: {}", request.email());
            return ResponseEntity.ok(new MessageResponse("Código de recuperación enviado a tu email"));

        } catch (UsuarioNotFoundException e) {
            log.warn("Intento de recuperación para email no existente: {}", request.email());
            return ResponseEntity.ok(new MessageResponse("Si el email existe, recibirás un código de recuperación"));
        } catch (Exception e) {
            log.error("Error en forgot-password: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error al procesar solicitud"));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            boolean isValid = passwordResetService.validateResetCode(request.email(), request.code());

            if (!isValid) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Código inválido o expirado"));
            }

            Usuario usuario = usuarioUseCase.obtenerPorEmail(request.email());
            usuario.setPassword(passwordEncoder.encode(request.newPassword()));
            usuarioRepository.actualizar(usuario);

            passwordResetService.invalidateCode(request.email());

            log.info("✅ Contraseña actualizada para: {}", request.email());
            return ResponseEntity.ok(new MessageResponse("Contraseña actualizada exitosamente"));

        } catch (UsuarioNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Usuario no encontrado"));
        } catch (Exception e) {
            log.error("Error en reset-password: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error al actualizar contraseña"));
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

    // ==================== VERIFICACIÓN DE EMAIL ====================

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        try {
            boolean isValid = emailVerificationService.validateCode(request.email(), request.code());

            if (!isValid) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Código inválido o expirado"));
            }

            // Marcar email como verificado
            emailVerificationService.markEmailAsVerified(request.email());

            // Obtener usuario y generar token para auto-login
            Usuario usuario = usuarioUseCase.obtenerPorEmail(request.email());

            // Si es ADMINISTRADOR, requiere 2FA - no dar token aún
            if (usuario.esAdministrador()) {
                log.info("✅ Email verificado para ADMIN: {} - Requiere 2FA para login", request.email());
                return ResponseEntity.ok(new AuthResponse(
                        null,
                        null,
                        usuario.getId(),
                        usuario.getNombreCompleto(),
                        usuario.getEmail(),
                        usuario.getDni(),
                        usuario.getRol().name(),
                        usuario.getPersona() != null && usuario.getPersona().getGenero() != null
                                ? usuario.getPersona().getGenero().name()
                                : "PREFIERO_NO_DECIR",
                        usuario.getActivo(),
                        usuario.getPersona() != null ? usuario.getPersona().getFotoPerfilUrl() : null,
                        null,
                        LocalDateTime.now(),
                        false,
                        "EMAIL_VERIFIED_ADMIN"));
            }

            // Para otros roles, generar token y hacer auto-login
            String token = jwtService.generateToken(usuario);
            LocalDateTime expiracion = jwtService.getExpirationFromToken(token);

            log.info("✅ Email verificado y auto-login para: {}", request.email());
            return ResponseEntity.ok(new AuthResponse(
                    token,
                    "Bearer",
                    usuario.getId(),
                    usuario.getNombreCompleto(),
                    usuario.getEmail(),
                    usuario.getDni(),
                    usuario.getRol().name(),
                    usuario.getPersona() != null && usuario.getPersona().getGenero() != null
                            ? usuario.getPersona().getGenero().name()
                            : "PREFIERO_NO_DECIR",
                    usuario.getActivo(),
                    usuario.getPersona() != null ? usuario.getPersona().getFotoPerfilUrl() : null,
                    expiracion,
                    LocalDateTime.now(),
                    false,
                    "Email verificado exitosamente"));

        } catch (UsuarioNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Usuario no encontrado"));
        } catch (Exception e) {
            log.error("Error en verificación de email: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error al verificar email"));
        }
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@Valid @RequestBody ResendVerificationRequest request) {
        try {
            emailVerificationService.resendVerificationCode(request.email());

            log.info("📧 Código de verificación reenviado a: {}", request.email());
            return ResponseEntity.ok(new MessageResponse("Código de verificación enviado"));

        } catch (UsuarioNotFoundException e) {
            // Por seguridad, no revelamos si el email existe o no
            return ResponseEntity.ok(new MessageResponse("Si el email existe, recibirás un código de verificación"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error reenviando verificación: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error al enviar código"));
        }
    }

    public record ErrorResponse(String message) {
    }

    public record MessageResponse(String message) {
    }
}