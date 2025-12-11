package com.gym.backend.Usuarios.Application;

import com.gym.backend.Auth.Domain.RegisterCommand;
import com.gym.backend.Usuarios.Domain.*;
import com.gym.backend.Usuarios.Domain.Enum.Genero;
import com.gym.backend.Usuarios.Domain.Enum.Rol;
import com.gym.backend.Usuarios.Domain.Exceptions.UsuarioDuplicateException;
import com.gym.backend.Usuarios.Infrastructure.Entity.*;
import com.gym.backend.Usuarios.Infrastructure.Jpa.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Servicio para el registro de usuarios con estructura normalizada
 * Crea: Usuario + Persona + Cliente/Empleado según el rol
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RegistroUsuarioService {

    private final UsuarioJpaRepository usuarioJpa;
    private final PersonaJpaRepository personaJpa;
    private final ClienteJpaRepository clienteJpa;
    private final EmpleadoJpaRepository empleadoJpa;
    private final EntrenadorJpaRepository entrenadorJpa;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registra un nuevo usuario con toda la estructura normalizada
     */
    @Transactional
    public Usuario registrarUsuarioCompleto(RegisterCommand command) {
        log.info("Registrando usuario completo: {}", command.email());

        // 1. Validar duplicados
        validarDuplicados(command.email(), command.dni());

        // 2. Determinar rol
        Rol rol = determinarRol(command.rol());
        Set<Rol> roles = new HashSet<>();
        roles.add(rol);

        // 3. Crear Usuario (solo auth)
        UsuarioEntity usuarioEntity = UsuarioEntity.builder()
                .email(command.email().trim().toLowerCase())
                .password(passwordEncoder.encode(command.password()))
                .activo(true)
                .emailVerificado(false)
                .roles(roles)
                .build();

        UsuarioEntity usuarioGuardado = usuarioJpa.save(usuarioEntity);
        log.info("✅ Usuario creado con ID: {}", usuarioGuardado.getId());

        // 4. Crear Persona (datos personales)
        Genero genero = command.genero() != null
                ? Genero.valueOf(command.genero().toUpperCase())
                : Genero.PREFIERO_NO_DECIR;

        PersonaEntity personaEntity = PersonaEntity.builder()
                .usuario(usuarioGuardado)
                .nombre(command.nombre().trim())
                .apellido(command.apellido().trim())
                .dni(command.dni().trim())
                .genero(genero)
                .telefono(command.telefono() != null ? command.telefono().trim() : null)
                .direccion(command.direccion() != null ? command.direccion().trim() : null)
                .build();

        PersonaEntity personaGuardada = personaJpa.save(personaEntity);
        log.info("✅ Persona creada con ID: {}", personaGuardada.getId());

        // 5. Crear Cliente o Empleado según el rol
        if (rol == Rol.CLIENTE) {
            crearCliente(personaGuardada, usuarioGuardado.getId());
        } else {
            crearEmpleado(personaGuardada, usuarioGuardado.getId(), rol);
        }

        // 6. Construir respuesta
        return construirUsuarioCompleto(usuarioGuardado, personaGuardada);
    }

    private void validarDuplicados(String email, String dni) {
        if (usuarioJpa.existsByEmail(email)) {
            throw new UsuarioDuplicateException("email", email);
        }
        if (personaJpa.existsByDni(dni)) {
            throw new UsuarioDuplicateException("dni", dni);
        }
    }

    private Rol determinarRol(String rolStr) {
        if (rolStr == null || rolStr.isBlank()) {
            return Rol.CLIENTE;
        }
        try {
            return Rol.valueOf(rolStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Rol no válido: {}, asignando CLIENTE por defecto", rolStr);
            return Rol.CLIENTE;
        }
    }

    private void crearCliente(PersonaEntity persona, Long usuarioId) {
        ClienteEntity cliente = ClienteEntity.builder()
                .persona(persona)
                .usuarioId(usuarioId)
                .activo(true)
                .fechaRegistroGym(LocalDate.now())
                .build();

        ClienteEntity guardado = clienteJpa.save(cliente);
        log.info("✅ Cliente creado con ID: {}", guardado.getId());
    }

    private void crearEmpleado(PersonaEntity persona, Long usuarioId, Rol rol) {
        // Generar código de empleado
        String codigoEmpleado = generarCodigoEmpleado(rol);

        EmpleadoEntity empleado = EmpleadoEntity.builder()
                .persona(persona)
                .usuarioId(usuarioId)
                .codigoEmpleado(codigoEmpleado)
                .fechaContratacion(LocalDate.now())
                .activo(true)
                .build();

        EmpleadoEntity empleadoGuardado = empleadoJpa.save(empleado);
        log.info("✅ Empleado creado con ID: {} - Código: {}", empleadoGuardado.getId(), codigoEmpleado);

        // Si es ENTRENADOR, crear registro adicional
        if (rol == Rol.ENTRENADOR) {
            EntrenadorEntity entrenador = EntrenadorEntity.builder()
                    .empleado(empleadoGuardado)
                    .usuarioId(usuarioId)
                    .activo(true)
                    .build();

            EntrenadorEntity entrenadorGuardado = entrenadorJpa.save(entrenador);
            log.info("✅ Entrenador creado con ID: {}", entrenadorGuardado.getId());
        }
    }

    private String generarCodigoEmpleado(Rol rol) {
        String prefijo = switch (rol) {
            case ADMINISTRADOR -> "ADM";
            case RECEPCIONISTA -> "REC";
            case ENTRENADOR -> "ENT";
            case CONTADOR -> "CON";
            default -> "EMP";
        };
        return prefijo + "-" + System.currentTimeMillis() % 100000;
    }

    private Usuario construirUsuarioCompleto(UsuarioEntity usuario, PersonaEntity persona) {
        return Usuario.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .password(usuario.getPassword())
                .activo(usuario.getActivo())
                .emailVerificado(usuario.getEmailVerificado())
                .roles(new HashSet<>(usuario.getRoles()))
                .fechaCreacion(usuario.getFechaCreacion())
                .persona(Persona.builder()
                        .id(persona.getId())
                        .usuarioId(usuario.getId())
                        .nombre(persona.getNombre())
                        .apellido(persona.getApellido())
                        .dni(persona.getDni())
                        .genero(persona.getGenero())
                        .telefono(persona.getTelefono())
                        .direccion(persona.getDireccion())
                        .build())
                .build();
    }
}
