package com.gym.backend.Usuarios.Domain;

import com.gym.backend.Usuarios.Domain.Enum.Genero;
import com.gym.backend.Usuarios.Domain.Enum.Rol;
import com.gym.backend.Usuarios.Domain.Exceptions.UsuarioValidationException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
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

    public void validar() {
        if (email == null || !email.contains("@")) {
            throw new UsuarioValidationException("Email inválido");
        }
        if (dni == null || dni.length() < 5) {
            throw new UsuarioValidationException("DNI debe tener al menos 5 caracteres");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new UsuarioValidationException("Nombre es requerido");
        }
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new UsuarioValidationException("Apellido es requerido");
        }
        if (password == null || password.trim().length() < 6) {
            throw new UsuarioValidationException("Password debe tener al menos 6 caracteres");
        }
    }

    public boolean esAdministrador() {
        return Rol.ADMINISTRADOR.equals(rol);
    }

    public boolean esActivo() {
        return activo != null && activo;
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