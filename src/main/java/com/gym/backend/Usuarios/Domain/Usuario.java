package com.gym.backend.Usuarios.Domain;

import com.gym.backend.Usuarios.Domain.Enum.Rol;
import com.gym.backend.Usuarios.Domain.Exceptions.UsuarioValidationException;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad de dominio para AUTENTICACIÓN únicamente
 * Los datos personales están en PERSONA
 * Los datos específicos de cliente/empleado están en sus respectivas entidades
 * 
 * NORMALIZADO según 2NF/3NF
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    private Long id;

    // Solo datos de autenticación
    private String email;
    private String password;
    private Boolean activo;
    private Boolean emailVerificado;
    private LocalDateTime ultimoLogin;

    // Roles (Many-to-Many)
    @Builder.Default
    private Set<Rol> roles = new HashSet<>();

    // Metadatos
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    // Referencias a entidades relacionadas (cargadas bajo demanda)
    private Persona persona;
    private Cliente cliente; // Solo si tiene rol CLIENTE
    private Empleado empleado; // Solo si tiene rol de empleado

    /**
     * Validar datos de autenticación
     */
    public void validar() {
        if (email == null || !email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new UsuarioValidationException("Email inválido");
        }
        if (password == null || password.trim().length() < 6) {
            throw new UsuarioValidationException("Password debe tener al menos 6 caracteres");
        }
    }

    // ==================== Verificación de roles ====================

    public boolean tieneRol(Rol rol) {
        return roles != null && roles.contains(rol);
    }

    public boolean esAdministrador() {
        return tieneRol(Rol.ADMINISTRADOR);
    }

    public boolean esCliente() {
        return tieneRol(Rol.CLIENTE);
    }

    public boolean esRecepcionista() {
        return tieneRol(Rol.RECEPCIONISTA);
    }

    public boolean esEntrenador() {
        return tieneRol(Rol.ENTRENADOR);
    }

    public boolean esContador() {
        return tieneRol(Rol.CONTADOR);
    }

    public boolean esEmpleado() {
        return esAdministrador() || esRecepcionista() || esEntrenador() || esContador();
    }

    // ==================== Estado ====================

    public boolean esActivo() {
        return Boolean.TRUE.equals(activo);
    }

    public boolean tieneEmailVerificado() {
        return Boolean.TRUE.equals(emailVerificado);
    }

    public void verificarEmail() {
        this.emailVerificado = true;
    }

    public void activar() {
        this.activo = true;
    }

    public void desactivar() {
        this.activo = false;
    }

    public void agregarRol(Rol rol) {
        if (this.roles == null) {
            this.roles = new HashSet<>();
        }
        this.roles.add(rol);
    }

    public void removerRol(Rol rol) {
        if (this.roles != null) {
            this.roles.remove(rol);
        }
    }

    // ==================== Datos desde Persona (compatibilidad)
    // ====================

    public String getNombre() {
        return persona != null ? persona.getNombre() : null;
    }

    public String getApellido() {
        return persona != null ? persona.getApellido() : null;
    }

    public String getDni() {
        return persona != null ? persona.getDni() : null;
    }

    public String getTelefono() {
        return persona != null ? persona.getTelefono() : null;
    }

    public String getDireccion() {
        return persona != null ? persona.getDireccion() : null;
    }

    public java.time.LocalDate getFechaNacimiento() {
        return persona != null ? persona.getFechaNacimiento() : null;
    }

    public String getNombreCompleto() {
        return persona != null ? persona.getNombreCompleto() : email;
    }

    /**
     * Obtener el rol principal (para compatibilidad con código antiguo)
     * Prioridad: ADMIN > RECEPCIONISTA > ENTRENADOR > CONTADOR > CLIENTE
     */
    public Rol getRol() {
        if (roles == null || roles.isEmpty())
            return null;
        if (roles.contains(Rol.ADMINISTRADOR))
            return Rol.ADMINISTRADOR;
        if (roles.contains(Rol.RECEPCIONISTA))
            return Rol.RECEPCIONISTA;
        if (roles.contains(Rol.ENTRENADOR))
            return Rol.ENTRENADOR;
        if (roles.contains(Rol.CONTADOR))
            return Rol.CONTADOR;
        if (roles.contains(Rol.CLIENTE))
            return Rol.CLIENTE;
        return roles.iterator().next();
    }

    /**
     * Setter para compatibilidad con código antiguo que usa un solo rol
     */
    public void setRol(Rol rol) {
        agregarRol(rol);
    }
}