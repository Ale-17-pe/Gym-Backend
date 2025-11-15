package com.gym.backend.Usuarios.Domain;

import com.gym.backend.Usuarios.Domain.Enum.Genero;
import com.gym.backend.Usuarios.Domain.Enum.Rol;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepositoryPort {
    Usuario guardar(Usuario usuario);
    Usuario actualizar(Usuario usuario);
    Optional<Usuario> buscarPorId(Long id);
    Optional<Usuario> buscarPorEmail(String email);
    Optional<Usuario> buscarPorDni(String dni);
    List<Usuario> listar();
    List<Usuario> listarPorRol(Rol rol);
    List<Usuario> listarPorGenero(Genero genero);
    List<Usuario> listarPorActivo(Boolean activo);
    void eliminar(Long id);
}