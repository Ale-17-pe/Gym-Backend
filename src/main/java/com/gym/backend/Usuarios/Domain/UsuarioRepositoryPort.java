package com.gym.backend.Usuarios.Domain;

import java.util.List;

public interface UsuarioRepositoryPort {

    Usuario guardar(Usuario usuario);

    Usuario actualizar(Long id, Usuario usuario);

    Usuario buscarPorId(Long id);

    Usuario buscarPorEmail(String email);

    Usuario buscarPorDni(String dni);

    List<Usuario> listar();

    void eliminar(Long id);
}
