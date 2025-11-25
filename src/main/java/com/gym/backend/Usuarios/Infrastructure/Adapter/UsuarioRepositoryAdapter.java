package com.gym.backend.Usuarios.Infrastructure.Adapter;

import com.gym.backend.Usuarios.Domain.Enum.Genero;
import com.gym.backend.Usuarios.Domain.Enum.Rol;
import com.gym.backend.Usuarios.Domain.Usuario;
import com.gym.backend.Usuarios.Domain.UsuarioRepositoryPort;
import com.gym.backend.Usuarios.Infrastructure.Entity.UsuarioEntity;
import com.gym.backend.Usuarios.Infrastructure.Jpa.UsuarioJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final UsuarioJpaRepository jpa;

    @Override
    public Usuario guardar(Usuario usuario) {
        return toDomain(jpa.save(toEntity(usuario)));
    }

    @Override
    public Usuario actualizar(Usuario usuario) {
        return jpa.findById(usuario.getId())
                .map(existente -> {
                    actualizarEntityDesdeDomain(existente, usuario);
                    return toDomain(jpa.save(existente));
                })
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado para actualizar"));
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return jpa.findByEmail(email).map(this::toDomain);
    }

    @Override
    public Optional<Usuario> buscarPorDni(String dni) {
        return jpa.findByDni(dni).map(this::toDomain);
    }

    @Override
    public List<Usuario> listar() {
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Page<Usuario> listarPaginated(Pageable pageable) {
        return jpa.findAll(pageable).map(this::toDomain);
    }

    @Override
    public List<Usuario> listarPorRol(Rol rol) {
        return jpa.findByRol(rol).stream().map(this::toDomain).toList();
    }

    @Override
    public Page<Usuario> listarPorRolPaginated(Rol rol, Pageable pageable) {
        return jpa.findByRol(rol, pageable).map(this::toDomain);
    }

    @Override
    public List<Usuario> listarPorGenero(Genero genero) {
        return jpa.findByGenero(genero).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Usuario> listarPorActivo(Boolean activo) {
        return jpa.findByActivo(activo).stream().map(this::toDomain).toList();
    }

    @Override
    public void eliminar(Long id) {
        jpa.deleteById(id);
    }

    private void actualizarEntityDesdeDomain(UsuarioEntity entity, Usuario domain) {
        entity.setNombre(domain.getNombre());
        entity.setApellido(domain.getApellido());
        entity.setGenero(domain.getGenero());
        entity.setTelefono(domain.getTelefono());
        entity.setDireccion(domain.getDireccion());
        entity.setRol(domain.getRol());
        entity.setActivo(domain.getActivo());
        entity.setFechaNacimiento(domain.getFechaNacimiento());

        if (domain.getPassword() != null && !domain.getPassword().trim().isEmpty()) {
            entity.setPassword(domain.getPassword());
        }
    }

    private Usuario toDomain(UsuarioEntity entity) {
        return Usuario.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .apellido(entity.getApellido())
                .genero(entity.getGenero())
                .email(entity.getEmail())
                .dni(entity.getDni())
                .telefono(entity.getTelefono())
                .direccion(entity.getDireccion())
                .password(entity.getPassword())
                .rol(entity.getRol())
                .activo(entity.getActivo())
                .fechaNacimiento(entity.getFechaNacimiento())
                .build();
    }

    private UsuarioEntity toEntity(Usuario domain) {
        return UsuarioEntity.builder()
                .id(domain.getId())
                .nombre(domain.getNombre())
                .apellido(domain.getApellido())
                .genero(domain.getGenero())
                .email(domain.getEmail())
                .dni(domain.getDni())
                .telefono(domain.getTelefono())
                .direccion(domain.getDireccion())
                .password(domain.getPassword())
                .rol(domain.getRol())
                .activo(domain.getActivo())
                .fechaNacimiento(domain.getFechaNacimiento())
                .build();
    }
}