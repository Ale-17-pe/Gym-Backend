package com.gym.backend.Usuarios.Infrastructure.Adapter;

import com.gym.backend.Usuarios.Domain.Usuario;
import com.gym.backend.Usuarios.Domain.UsuarioRepositoryPort;
import com.gym.backend.Usuarios.Infrastructure.Entity.UsuarioEntity;
import com.gym.backend.Usuarios.Infrastructure.Jpa.UsuarioJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final UsuarioJpaRepository jpa;

    public UsuarioRepositoryAdapter(UsuarioJpaRepository jpa) {
        this.jpa = jpa;
    }

    private Usuario toDomain(UsuarioEntity e) {
        return Usuario.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .apellido(e.getApellido())
                .email(e.getEmail())
                .dni(e.getDni())
                .telefono(e.getTelefono())
                .direccion(e.getDireccion())
                .password(e.getPassword())
                .rol(e.getRol())
                .activo(e.getActivo())
                .build();
    }

    private UsuarioEntity toEntity(Usuario d) {
        return UsuarioEntity.builder()
                .id(d.getId())
                .nombre(d.getNombre())
                .apellido(d.getApellido())
                .email(d.getEmail())
                .dni(d.getDni())
                .telefono(d.getTelefono())
                .direccion(d.getDireccion())
                .password(d.getPassword())
                .rol(d.getRol())
                .activo(d.getActivo())
                .build();
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        return toDomain(jpa.save(toEntity(usuario)));
    }

    @Override
    public Usuario actualizar(Long id, Usuario usuario) {
        UsuarioEntity e = jpa.findById(id)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));

        e.setNombre(usuario.getNombre());
        e.setApellido(usuario.getApellido());
        e.setTelefono(usuario.getTelefono());
        e.setDireccion(usuario.getDireccion());
        e.setRol(usuario.getRol());

        return toDomain(jpa.save(e));
    }

    @Override
    public Usuario buscarPorId(Long id) {
        return toDomain(
                jpa.findById(id)
                        .orElseThrow(() -> new IllegalStateException("No existe el usuario"))
        );
    }

    @Override
    public Usuario buscarPorEmail(String email) {
        var e = jpa.findByEmail(email);
        return e != null ? toDomain(e) : null;
    }

    @Override
    public Usuario buscarPorDni(String dni) {
        var e = jpa.findByDni(dni);
        return e != null ? toDomain(e) : null;
    }

    @Override
    public List<Usuario> listar() {
        return jpa.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void eliminar(Long id) {
        jpa.deleteById(id);
    }
}
