package com.gym.backend.Usuarios.Application.Mapper;

import com.gym.backend.Usuarios.Application.Dto.UsuarioDTO;
import com.gym.backend.Usuarios.Application.Dto.UsuarioResponse;
import com.gym.backend.Usuarios.Domain.Persona;
import com.gym.backend.Usuarios.Domain.Usuario;
import org.springframework.stereotype.Component;

import java.util.HashSet;

/**
 * Mapper manual para Usuario (nueva estructura normalizada)
 * MapStruct no maneja bien la relación con Persona
 */
@Component
public class UsuarioMapper {

    public UsuarioDTO toDTO(Usuario usuario) {
        if (usuario == null)
            return null;

        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setEmail(usuario.getEmail());
        dto.setActivo(usuario.getActivo());
        dto.setEmailVerificado(usuario.getEmailVerificado());
        dto.setRol(usuario.getRol() != null ? usuario.getRol().name() : null);

        // Datos desde Persona
        if (usuario.getPersona() != null) {
            Persona p = usuario.getPersona();
            dto.setNombre(p.getNombre());
            dto.setApellido(p.getApellido());
            dto.setDni(p.getDni());
            dto.setGenero(p.getGenero() != null ? p.getGenero().name() : null);
            dto.setTelefono(p.getTelefono());
            dto.setDireccion(p.getDireccion());
            dto.setFechaNacimiento(p.getFechaNacimiento());
        }

        return dto;
    }

    public UsuarioResponse toResponse(Usuario usuario) {
        if (usuario == null)
            return null;

        return UsuarioResponse.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .nombreCompleto(usuario.getNombreCompleto())
                .dni(usuario.getDni())
                .genero(usuario.getPersona() != null && usuario.getPersona().getGenero() != null
                        ? usuario.getPersona().getGenero().name()
                        : null)
                .telefono(usuario.getTelefono())
                .direccion(usuario.getDireccion())
                .rol(usuario.getRol() != null ? usuario.getRol().name() : null)
                .activo(usuario.getActivo())
                .emailVerificado(usuario.getEmailVerificado())
                .fechaNacimiento(usuario.getFechaNacimiento())
                .fotoPerfilUrl(usuario.getPersona() != null ? usuario.getPersona().getFotoPerfilUrl() : null)
                .build();
    }
}