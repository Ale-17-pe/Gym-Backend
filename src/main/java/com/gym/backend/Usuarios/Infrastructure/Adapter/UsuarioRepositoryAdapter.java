package com.gym.backend.Usuarios.Infrastructure.Adapter;

import com.gym.backend.Usuarios.Domain.Enum.Genero;
import com.gym.backend.Usuarios.Domain.Enum.Rol;
import com.gym.backend.Usuarios.Domain.Usuario;
import com.gym.backend.Usuarios.Domain.UsuarioRepositoryPort;
import com.gym.backend.Usuarios.Infrastructure.Entity.UsuarioEntity;
import com.gym.backend.Usuarios.Infrastructure.Jpa.UsuarioJpaRepository;
import lombok.RequiredArgsConstructor;
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
        UsuarioEntity existente = jpa.findById(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado para actualizar"));

        existente.setNombre(usuario.getNombre());
        existente.setApellido(usuario.getApellido());
        existente.setGenero(usuario.getGenero());
        existente.setTelefono(usuario.getTelefono());
        existente.setDireccion(usuario.getDireccion());
        existente.setRol(usuario.getRol());
        existente.setActivo(usuario.getActivo());

        if (usuario.getPassword() != null && !usuario.getPassword().trim().isEmpty()) {
            existente.setPassword(usuario.getPassword());
        }

        return toDomain(jpa.save(existente));
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
    public List<Usuario> listarPorRol(Rol rol) {
        return jpa.findByRol(rol).stream().map(this::toDomain).toList();
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
    public void eliminar(Long id) { jpa.deleteById(id); }

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
                .build();
    }
}