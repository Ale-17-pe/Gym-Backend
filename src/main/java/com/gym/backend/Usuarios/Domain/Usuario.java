package com.gym.backend.Usuarios.Domain;

import com.gym.backend.Usuarios.Domain.Enum.Genero;
import com.gym.backend.Usuarios.Domain.Enum.Rol;
import com.gym.backend.Usuarios.Domain.Exceptions.UsuarioValidationException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Usuario {

    private Long id;
    private String nombre;
    private String apellido;
    private Genero genero;
    private String email;
    private String dni;
    private String telefono;
    private String direccion;
    private String password;
    private Rol rol;
    private Boolean activo;
    private java.time.LocalDate fechaNacimiento;
    @Builder.Default
    private Boolean emailVerificado = false;

    public void validar() {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new UsuarioValidationException("Nombre es requerido");
        }
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new UsuarioValidationException("Apellido es requerido");
        }
        if (password == null || password.trim().length() < 6) {
            throw new UsuarioValidationException("Password debe tener al menos 6 caracteres");
        }
        if (dni == null || !dni.matches("\\d{8,15}")) {
            throw new UsuarioValidationException("DNI debe contener solo números y tener entre 8-15 dígitos");
        }
    }

    public boolean esAdministrador() {
        return Rol.ADMINISTRADOR.equals(rol);
    }

    public boolean esActivo() {
        return Boolean.TRUE.equals(activo);
    }

    public boolean tieneEmailVerificado() {
        return Boolean.TRUE.equals(emailVerificado);
    }

    public void verificarEmail() {
        this.emailVerificado = true;
    }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public void activar() {
        this.activo = true;
    }

    public void desactivar() {
        this.activo = false;
    }
}