package com.gym.backend.Usuarios.Infrastructure.Adapter;

import com.gym.backend.Usuarios.Domain.Persona;
import com.gym.backend.Usuarios.Domain.PersonaRepositoryPort;
import com.gym.backend.Usuarios.Infrastructure.Entity.PersonaEntity;
import com.gym.backend.Usuarios.Infrastructure.Entity.UsuarioEntity;
import com.gym.backend.Usuarios.Infrastructure.Jpa.PersonaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PersonaRepositoryAdapter implements PersonaRepositoryPort {

    private final PersonaJpaRepository jpa;

    @Override
    public Persona guardar(Persona persona) {
        return toDomain(jpa.save(toEntity(persona)));
    }

    @Override
    public Optional<Persona> buscarPorId(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Persona> buscarPorUsuarioId(Long usuarioId) {
        return jpa.findByUsuarioId(usuarioId).map(this::toDomain);
    }

    @Override
    public Optional<Persona> buscarPorDni(String dni) {
        return jpa.findByDni(dni).map(this::toDomain);
    }

    @Override
    public boolean existePorDni(String dni) {
        return jpa.existsByDni(dni);
    }

    @Override
    public List<Persona> buscarCumpleanosHoy(LocalDate fecha) {
        return jpa.findCumpleanosHoy(fecha).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Persona> buscarPorNombreOApellido(String termino) {
        return jpa.buscarPorNombreOApellido(termino).stream().map(this::toDomain).toList();
    }

    @Override
    public void eliminar(Long id) {
        jpa.deleteById(id);
    }

    private Persona toDomain(PersonaEntity entity) {
        if (entity == null)
            return null;

        return Persona.builder()
                .id(entity.getId())
                .usuarioId(entity.getUsuario() != null ? entity.getUsuario().getId() : null)
                .nombre(entity.getNombre())
                .apellido(entity.getApellido())
                .dni(entity.getDni())
                .genero(entity.getGenero())
                .fechaNacimiento(entity.getFechaNacimiento())
                .telefono(entity.getTelefono())
                .direccion(entity.getDireccion())
                .fotoPerfilUrl(entity.getFotoPerfilUrl())
                .fechaCreacion(entity.getFechaCreacion())
                .fechaActualizacion(entity.getFechaActualizacion())
                .build();
    }

    private PersonaEntity toEntity(Persona persona) {
        PersonaEntity entity = PersonaEntity.builder()
                .id(persona.getId())
                .nombre(persona.getNombre())
                .apellido(persona.getApellido())
                .dni(persona.getDni())
                .genero(persona.getGenero())
                .fechaNacimiento(persona.getFechaNacimiento())
                .telefono(persona.getTelefono())
                .direccion(persona.getDireccion())
                .fotoPerfilUrl(persona.getFotoPerfilUrl())
                .fechaCreacion(persona.getFechaCreacion())
                .fechaActualizacion(persona.getFechaActualizacion())
                .build();

        if (persona.getUsuarioId() != null) {
            UsuarioEntity usuario = UsuarioEntity.builder()
                    .id(persona.getUsuarioId())
                    .build();
            entity.setUsuario(usuario);
        }

        return entity;
    }
}
