package com.gym.backend.Auth.Infrastructure;

import com.gym.backend.Auth.Domain.AuthResponse;
import com.gym.backend.Auth.Domain.AuthServicePort;
import com.gym.backend.Auth.Domain.LoginCommand;
import com.gym.backend.Auth.Domain.RegisterCommand;
import com.gym.backend.Shared.Security.JwtService;
import com.gym.backend.Shared.TwoFactorAuth.TwoFactorAuthService;
import com.gym.backend.Usuarios.Application.RegistroUsuarioService;
import com.gym.backend.Usuarios.Domain.Enum.Rol;
import com.gym.backend.Usuarios.Domain.Exceptions.*;
import com.gym.backend.Usuarios.Domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceAdapter implements AuthServicePort {

    private final UsuarioRepositoryPort usuarioRepository;
    private final PersonaRepositoryPort personaRepository;
    private final RegistroUsuarioService registroService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TwoFactorAuthService twoFactorAuthService;
    private final com.gym.backend.Shared.Email.EmailService emailService;

    @Override
    public AuthResponse login(LoginCommand command) {
        log.info("Intentando login con: {}", command.emailOrDni());

        Usuario usuario = buscarUsuarioPorEmailODni(command.emailOrDni());

        if (usuario == null) {
            log.warn("Login fallido - Usuario no encontrado: {}", command.emailOrDni());
            throw new UsuarioNotFoundException("Credenciales inválidas");
        }

        if (!usuario.esActivo()) {
            log.warn("Login fallido - Usuario inactivo: {}", command.emailOrDni());
            throw new UsuarioInactiveException(usuario.getId());
        }

        if (!passwordEncoder.matches(command.password(), usuario.getPassword())) {
            log.warn("Login fallido - Contraseña incorrecta para: {}", command.emailOrDni());
            throw new UsuarioValidationException("Credenciales inválidas");
        }

        // PASO 1: Verificar si el email está verificado
        if (!usuario.tieneEmailVerificado()) {
            log.warn("Login fallido - Email no verificado para: {}", command.emailOrDni());
            return new AuthResponse(
                    null,
                    null,
                    usuario.getId(),
                    usuario.getNombreCompleto(),
                    usuario.getEmail(),
                    usuario.getDni(),
                    usuario.getRol().name(),
                    usuario.getPersona() != null ? usuario.getPersona().getGenero().name() : "PREFIERO_NO_DECIR",
                    usuario.getActivo(),
                    usuario.tieneEmailVerificado(),
                    usuario.getPersona() != null ? usuario.getPersona().getFotoPerfilUrl() : null,
                    usuario.getPersona() != null ? usuario.getPersona().getTelefono() : null,
                    usuario.getPersona() != null ? usuario.getPersona().getDireccion() : null,
                    null,
                    LocalDateTime.now(),
                    false,
                    "EMAIL_NOT_VERIFIED");
        }

        // PASO 2: Si es ADMINISTRADOR - requiere 2FA
        if (usuario.esAdministrador()) {
            log.info("Usuario ADMIN detectado, generando código 2FA: {}", usuario.getEmail());
            twoFactorAuthService.generateCode(usuario.getEmail());

            return new AuthResponse(
                    null,
                    null,
                    usuario.getId(),
                    usuario.getNombreCompleto(),
                    usuario.getEmail(),
                    usuario.getDni(),
                    usuario.getRol().name(),
                    usuario.getPersona() != null ? usuario.getPersona().getGenero().name() : "PREFIERO_NO_DECIR",
                    usuario.getActivo(),
                    usuario.tieneEmailVerificado(),
                    usuario.getPersona() != null ? usuario.getPersona().getFotoPerfilUrl() : null,
                    usuario.getPersona() != null ? usuario.getPersona().getTelefono() : null,
                    usuario.getPersona() != null ? usuario.getPersona().getDireccion() : null,
                    null,
                    LocalDateTime.now(),
                    true,
                    "Código 2FA enviado a tu email.");
        }

        // Login normal para otros roles
        String token = jwtService.generateToken(usuario);
        LocalDateTime expiracion = jwtService.getExpirationFromToken(token);

        log.info("Login exitoso para usuario: {}", usuario.getEmail());

        return new AuthResponse(
                token,
                "Bearer",
                usuario.getId(),
                usuario.getNombreCompleto(),
                usuario.getEmail(),
                usuario.getDni(),
                usuario.getRol().name(),
                usuario.getPersona() != null ? usuario.getPersona().getGenero().name() : "PREFIERO_NO_DECIR",
                usuario.getActivo(),
                usuario.tieneEmailVerificado(),
                usuario.getPersona() != null ? usuario.getPersona().getFotoPerfilUrl() : null,
                usuario.getPersona() != null ? usuario.getPersona().getTelefono() : null,
                usuario.getPersona() != null ? usuario.getPersona().getDireccion() : null,
                expiracion,
                LocalDateTime.now(),
                false,
                null);
    }

    @Override
    public AuthResponse registrar(RegisterCommand command) {
        log.info("Registrando nuevo usuario: {}", command.email());

        try {
            // Usar el nuevo servicio de registro que crea Usuario + Persona +
            // Cliente/Empleado
            Usuario usuarioCreado = registroService.registrarUsuarioCompleto(command);

            // Log de confirmación en consola
            logNuevoUsuarioRegistrado(usuarioCreado);

            // Enviar email de bienvenida
            try {
                emailService.enviarEmailBienvenida(usuarioCreado.getId());
                log.info("Email de bienvenida enviado a: {}", usuarioCreado.getEmail());
            } catch (Exception e) {
                log.warn("No se pudo enviar email de bienvenida: {}", e.getMessage());
            }

            String token = jwtService.generateToken(usuarioCreado);
            LocalDateTime expiracion = jwtService.getExpirationFromToken(token);

            log.info("Usuario registrado exitosamente: {}", command.email());

            return new AuthResponse(
                    token,
                    "Bearer",
                    usuarioCreado.getId(),
                    usuarioCreado.getNombreCompleto(),
                    usuarioCreado.getEmail(),
                    usuarioCreado.getDni(),
                    usuarioCreado.getRol().name(),
                    usuarioCreado.getPersona() != null ? usuarioCreado.getPersona().getGenero().name()
                            : "PREFIERO_NO_DECIR",
                    usuarioCreado.getActivo(),
                    usuarioCreado.tieneEmailVerificado(),
                    usuarioCreado.getPersona() != null ? usuarioCreado.getPersona().getFotoPerfilUrl() : null,
                    usuarioCreado.getPersona() != null ? usuarioCreado.getPersona().getTelefono() : null,
                    usuarioCreado.getPersona() != null ? usuarioCreado.getPersona().getDireccion() : null,
                    expiracion,
                    LocalDateTime.now(),
                    false,
                    null);

        } catch (UsuarioDuplicateException e) {
            log.warn("Registro fallido - {}: {}", e.getCode(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error en registro: {}", e.getMessage(), e);
            throw new UsuarioValidationException("Error en el registro: " + e.getMessage());
        }
    }

    @Override
    public void validarToken(String token) {
        if (!jwtService.isTokenValid(token)) {
            throw new UsuarioValidationException("Token inválido o expirado");
        }
    }

    private Usuario buscarUsuarioPorEmailODni(String emailOrDni) {
        // Buscar por email con datos completos
        var porEmail = usuarioRepository.buscarPorEmailConDatosCompletos(emailOrDni);
        if (porEmail.isPresent()) {
            return porEmail.get();
        }

        // Buscar por DNI
        var persona = personaRepository.buscarPorDni(emailOrDni);
        if (persona.isPresent()) {
            return usuarioRepository.buscarPorIdConDatosCompletos(persona.get().getUsuarioId()).orElse(null);
        }

        return null;
    }

    private void logNuevoUsuarioRegistrado(Usuario usuario) {
        log.info("╔════════════════════════════════════════╗");
        log.info("║   NUEVO USUARIO REGISTRADO             ║");
        log.info("╠════════════════════════════════════════╣");
        log.info("║   Email:  {:<28}║", usuario.getEmail());
        log.info("║   DNI:    {:<28}║", usuario.getDni());
        log.info("║   Nombre: {:<28}║", usuario.getNombreCompleto());
        log.info("║   Rol:    {:<28}║", usuario.getRol().name());
        log.info("╚════════════════════════════════════════╝");
    }
}
