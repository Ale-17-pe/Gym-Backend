package com.gym.backend.Usuarios.Domain;

import com.gym.backend.Shared.PasswordReset.PasswordResetService;
import com.gym.backend.Usuarios.Application.Dto.ActualizarUsuarioRequest;
import com.gym.backend.Usuarios.Domain.Enum.Rol;
import com.gym.backend.Usuarios.Domain.Exceptions.UsuarioDuplicateException;
import com.gym.backend.Usuarios.Domain.Exceptions.UsuarioInactiveException;
import com.gym.backend.Usuarios.Domain.Exceptions.UsuarioNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepo;
    private final PersonaRepositoryPort personaRepo;
    private final ClienteRepositoryPort clienteRepo;
    private final PasswordResetService passwordResetService;

    /**
     * Crear usuario (solo para uso interno o migraciones)
     * Para registro de nuevos usuarios usar RegistroUsuarioService
     */
    public Usuario crear(Usuario usuario) {
        log.info("Creando usuario: {}", usuario.getEmail());
        usuario.validar();

        if (usuarioRepo.buscarPorEmail(usuario.getEmail()).isPresent()) {
            throw new UsuarioDuplicateException("email", usuario.getEmail());
        }

        // Verificar DNI en tabla Persona
        if (usuario.getPersona() != null && usuario.getPersona().getDni() != null) {
            if (personaRepo.existePorDni(usuario.getPersona().getDni())) {
                throw new UsuarioDuplicateException("dni", usuario.getPersona().getDni());
            }
        }

        return usuarioRepo.guardar(usuario);
    }

    @Transactional
    public Usuario actualizar(Long id, ActualizarUsuarioRequest request) {
        log.info("Actualizando usuario ID: {}", id);
        Usuario existente = obtenerConDatosCompletos(id);

        // Actualizar datos de Usuario
        if (request.getActivo() != null) {
            existente.setActivo(request.getActivo());
        }
        if (request.getRol() != null) {
            existente.agregarRol(Rol.valueOf(request.getRol().toUpperCase()));
        }

        // Actualizar Persona si existe
        if (existente.getPersona() != null) {
            Persona persona = existente.getPersona();
            if (request.getNombre() != null)
                persona.setNombre(request.getNombre());
            if (request.getApellido() != null)
                persona.setApellido(request.getApellido());
            if (request.getGenero() != null)
                persona.setGenero(request.getGenero());
            if (request.getTelefono() != null)
                persona.setTelefono(request.getTelefono());
            if (request.getDireccion() != null)
                persona.setDireccion(request.getDireccion());
            if (request.getFechaNacimiento() != null)
                persona.setFechaNacimiento(request.getFechaNacimiento());

            personaRepo.guardar(persona);
        }

        return usuarioRepo.actualizar(existente);
    }

    public Usuario obtener(Long id) {
        log.debug("Obteniendo usuario ID: {}", id);
        return usuarioRepo.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNotFoundException(id));
    }

    public Usuario obtenerConDatosCompletos(Long id) {
        return usuarioRepo.buscarPorIdConDatosCompletos(id)
                .orElseThrow(() -> new UsuarioNotFoundException(id));
    }

    public Usuario obtenerPorEmail(String email) {
        return usuarioRepo.buscarPorEmailConDatosCompletos(email)
                .orElseThrow(() -> new UsuarioNotFoundException(email));
    }

    public Usuario obtenerPorDni(String dni) {
        return usuarioRepo.buscarPorDni(dni)
                .orElseThrow(() -> new UsuarioNotFoundException("DNI: " + dni));
    }

    public List<Usuario> listar() {
        return usuarioRepo.listar();
    }

    public Page<Usuario> listarPaginated(Pageable pageable) {
        return usuarioRepo.listarPaginated(pageable);
    }

    public List<Usuario> listarActivos() {
        return usuarioRepo.listarPorActivo(true);
    }

    public List<Usuario> listarInactivos() {
        return usuarioRepo.listarPorActivo(false);
    }

    public List<Usuario> listarPorRol(Rol rol) {
        return usuarioRepo.listarPorRol(rol);
    }

    /**
     * Listar todos los clientes
     */
    public List<Cliente> listarClientes() {
        return clienteRepo.listar();
    }

    /**
     * Listar clientes activos
     */
    public List<Cliente> listarClientesActivos() {
        return clienteRepo.listarActivos();
    }

    public void eliminar(Long id) {
        obtener(id);
        usuarioRepo.eliminar(id);
    }

    public Usuario desactivar(Long id) {
        Usuario usuario = obtener(id);
        usuario.desactivar();
        return usuarioRepo.actualizar(usuario);
    }

    public Usuario activar(Long id) {
        Usuario usuario = obtener(id);
        usuario.activar();
        return usuarioRepo.actualizar(usuario);
    }

    public void verificarUsuarioActivo(Long id) {
        Usuario usuario = obtener(id);
        if (!usuario.esActivo()) {
            throw new UsuarioInactiveException(id);
        }
    }

    /**
     * Marcar email como verificado manualmente (admin)
     */
    @Transactional
    public Usuario marcarEmailVerificado(Long id) {
        log.info("Marcando email como verificado para usuario ID: {}", id);
        Usuario usuario = obtener(id);
        usuario.setEmailVerificado(true);
        return usuarioRepo.actualizar(usuario);
    }

    /**
     * Enviar código de reset de password
     */
    public void enviarResetPassword(Long id) {
        Usuario usuario = obtenerConDatosCompletos(id);
        log.info("Enviando código de reset a: {}", usuario.getEmail());
        passwordResetService.generateResetCode(usuario.getEmail());
    }

    /**
     * Actualizar avatar del usuario
     */
    @Transactional
    public Usuario actualizarAvatar(Long id, String avatarUrl) {
        log.info("Actualizando avatar para usuario ID: {}", id);
        Usuario usuario = obtenerConDatosCompletos(id);

        if (usuario.getPersona() != null) {
            usuario.getPersona().setFotoPerfilUrl(avatarUrl);
            personaRepo.guardar(usuario.getPersona());
        }

        return usuario;
    }
}