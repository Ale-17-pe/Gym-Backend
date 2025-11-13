package com.gym.backend.Usuarios.Domain;


import java.util.List;

public class UsuarioUseCase {

    private final UsuarioRepositoryPort repo;

    public UsuarioUseCase(UsuarioRepositoryPort repo) {
        this.repo = repo;
    }

    public Usuario crear(Usuario usuario) {
        return repo.guardar(usuario);
    }

    public Usuario actualizar(Long id, Usuario usuario) {
        return repo.actualizar(id, usuario);
    }

    public Usuario obtener(Long id) {
        return repo.buscarPorId(id);
    }

    public List<Usuario> listar() {
        return repo.listar();
    }

    public void eliminar(Long id) {
        repo.eliminar(id);
    }
}