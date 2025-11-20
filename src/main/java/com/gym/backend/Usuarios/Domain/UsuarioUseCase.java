package com.gym.backend.Usuarios.Domain;


import com.gym.backend.Usuarios.Application.Dto.ActualizarUsuarioRequest;
import com.gym.backend.Usuarios.Domain.Enum.Genero;
import com.gym.backend.Usuarios.Domain.Enum.Rol;
import com.gym.backend.Usuarios.Domain.Exceptions.UsuarioDuplicateException;
import com.gym.backend.Usuarios.Domain.Exceptions.UsuarioInactiveException;
import com.gym.backend.Usuarios.Domain.Exceptions.UsuarioNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioUseCase {

    private final UsuarioRepositoryPort repo;

    public Usuario crear(Usuario usuario) {
        log.info("Creando usuario: {}", usuario.getEmail());
        usuario.validar();

        if (repo.buscarPorEmail(usuario.getEmail()).isPresent()) {
            throw new UsuarioDuplicateException("email", usuario.getEmail());
        }
        if (repo.buscarPorDni(usuario.getDni()).isPresent()) {
            throw new UsuarioDuplicateException("dni", usuario.getDni());
        }

        return repo.guardar(usuario);
    }

    public Usuario actualizar(Long id, ActualizarUsuarioRequest request) {
        log.info("Actualizando usuario ID: {}", id);
        Usuario existente = obtener(id);

        // Actualizar solo campos permitidos
        existente.setNombre(request.getNombre());
        existente.setApellido(request.getApellido());
        existente.setGenero(request.getGenero());
        existente.setTelefono(request.getTelefono());
        existente.setDireccion(request.getDireccion());

        if (request.getRol() != null) {
            existente.setRol(Rol.valueOf(request.getRol().toUpperCase()));
        }
        if (request.getActivo() != null) {
            existente.setActivo(request.getActivo());
        }

        existente.validar();
        return repo.actualizar(existente);
    }

    public Usuario obtener(Long id) {
        log.debug("Obteniendo usuario ID: {}", id);
        return repo.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNotFoundException(id));
    }

    public Usuario obtenerPorEmail(String email) {
        return repo.buscarPorEmail(email)
                .orElseThrow(() -> new UsuarioNotFoundException(email));
    }

    public Usuario obtenerPorDni(String dni) {
        return repo.buscarPorDni(dni)
                .orElseThrow(() -> new UsuarioNotFoundException("DNI: " + dni));
    }

    public List<Usuario> listar() {
        return repo.listar();
    }

    public Page<Usuario> listarPaginated(Pageable pageable) {
        return repo.listarPaginated(pageable);
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