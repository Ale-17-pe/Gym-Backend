package com.gym.backend.Usuarios.Domain;


import com.gym.backend.Usuarios.Domain.Enum.Genero;
import com.gym.backend.Usuarios.Domain.Enum.Rol;
import com.gym.backend.Usuarios.Domain.Exceptions.UsuarioDuplicateException;
import com.gym.backend.Usuarios.Domain.Exceptions.UsuarioInactiveException;
import com.gym.backend.Usuarios.Domain.Exceptions.UsuarioNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioUseCase {

    private final UsuarioRepositoryPort repo;

    public UsuarioUseCase(UsuarioRepositoryPort repo) {
        this.repo = repo;
    }

    public Usuario crear(Usuario usuario) {
        usuario.validar();

        if (repo.buscarPorEmail(usuario.getEmail()).isPresent()) {
            throw new UsuarioDuplicateException("email", usuario.getEmail());
        }
        if (repo.buscarPorDni(usuario.getDni()).isPresent()) {
            throw new UsuarioDuplicateException("dni", usuario.getDni());
        }

        return repo.guardar(usuario);
    }

    public Usuario actualizar(Usuario usuario) {
        Usuario existente = obtener(usuario.getId());
        usuario.validar();
        return repo.actualizar(usuario);
    }

    public Usuario obtener(Long id) {
        return repo.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNotFoundException(id));
    }

    public Usuario obtenerPorEmail(String email) {
        return repo.buscarPorEmail(email)
                .orElseThrow(() -> new UsuarioNotFoundException(email));
    }

    public Usuario obtenerPorDni(String dni) {
        return repo.buscarPorDni(dni)
                .orElseThrow(() -> new UsuarioDuplicateException("dni", dni));
    }

    public List<Usuario> listar() {
        return repo.listar();
    }

    public List<Usuario> listarActivos() {
        return repo.listarPorActivo(true);
    }

    public List<Usuario> listarInactivos() {
        return repo.listarPorActivo(false);
    }

    public List<Usuario> listarPorRol(Rol rol) {
        return repo.listarPorRol(rol);
    }

    public List<Usuario> listarPorGenero(Genero genero) {
        return repo.listarPorGenero(genero);
    }

    public void eliminar(Long id) {
        obtener(id); // Verificar que existe
        repo.eliminar(id);
    }

    public Usuario desactivar(Long id) {
        Usuario usuario = obtener(id);
        usuario.desactivar();
        return repo.actualizar(usuario);
    }

    public Usuario activar(Long id) {
        Usuario usuario = obtener(id);
        usuario.activar();
        return repo.actualizar(usuario);
    }

    public void verificarUsuarioActivo(Long id) {
        Usuario usuario = obtener(id);
        if (!usuario.esActivo()) {
            throw new UsuarioInactiveException(id);
        }
    }
}