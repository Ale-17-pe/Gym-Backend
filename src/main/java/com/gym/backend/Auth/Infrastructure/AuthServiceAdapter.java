package com.gym.backend.Auth.Infrastructure;

import com.gym.backend.Auth.Domain.AuthResponse;
import com.gym.backend.Auth.Domain.AuthServicePort;
import com.gym.backend.Auth.Domain.LoginCommand;
import com.gym.backend.Auth.Domain.RegisterCommand;
import com.gym.backend.Shared.Security.JwtService;
import com.gym.backend.Usuarios.Domain.Enum.Genero;
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

    private final UsuarioUseCase usuarioUseCase;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

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
                usuario.getGenero().name(),
                usuario.getActivo(),
                expiracion,
                LocalDateTime.now());
    }

    @Override
    public AuthResponse registrar(RegisterCommand command) {
        log.info("Registrando nuevo usuario: {}", command.email());

        try {
            Genero genero = command.genero() != null ? Genero.valueOf(command.genero().toUpperCase())
                    : Genero.PREFIERO_NO_DECIR;

            Rol rol = validarYAsignarRol(command.rol());

            Usuario usuario = Usuario.builder()
                    .nombre(command.nombre().trim())
                    .apellido(command.apellido().trim())
                    .genero(genero)
                    .email(command.email().trim().toLowerCase())
                    .dni(command.dni().trim())
                    .telefono(command.telefono() != null ? command.telefono().trim() : null)
                    .direccion(command.direccion() != null ? command.direccion().trim() : null)
                    .password(passwordEncoder.encode(command.password()))
                    .rol(rol)
                    .activo(true)
                    .build();

            Usuario usuarioCreado = usuarioUseCase.crear(usuario);
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
                    usuarioCreado.getGenero().name(),
                    usuarioCreado.getActivo(),
                    expiracion,
                    LocalDateTime.now());

        } catch (UsuarioDuplicateException e) {
            log.warn("Registro fallido - {}: {}", e.getCode(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error en registro: {}", e.getMessage());
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
        try {
            return usuarioUseCase.obtenerPorEmail(emailOrDni);
        } catch (UsuarioNotFoundException e) {
            try {
                return usuarioUseCase.obtenerPorDni(emailOrDni);
            } catch (UsuarioNotFoundException ex) {
                return null;
            }
        }
    }

    private Rol validarYAsignarRol(String rol) {
        if (rol == null || rol.isBlank())
            return Rol.CLIENTE;
        try {
            return Rol.valueOf(rol.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Rol no válido: {}, asignando CLIENTE por defecto", rol);
            return Rol.CLIENTE;
        }
    }
}