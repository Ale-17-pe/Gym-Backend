package com.gym.backend.Usuarios.Domain;

import com.gym.backend.Usuarios.Domain.Enum.Rol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Puerto del repositorio de Usuarios (solo autenticación)
 * Los datos personales están en PersonaRepositoryPort
 */
public interface UsuarioRepositoryPort {

    // ==================== CRUD ====================

    Usuario guardar(Usuario usuario);

    Usuario actualizar(Usuario usuario);

    Optional<Usuario> buscarPorId(Long id);

    void eliminar(Long id);

    // ==================== BÚSQUEDAS ====================

    Optional<Usuario> buscarPorEmail(String email);

    /**
     * Busca por DNI a través de la relación con Persona
     */
    Optional<Usuario> buscarPorDni(String dni);

    // ==================== LISTADOS ====================

    List<Usuario> listar();

    Page<Usuario> listarPaginated(Pageable pageable);

    List<Usuario> listarPorRol(Rol rol);

    Page<Usuario> listarPorRolPaginated(Rol rol, Pageable pageable);

    List<Usuario> listarPorActivo(Boolean activo);

    // ==================== OPERACIONES DE CARGA COMPLETA ====================

    /**
     * Busca usuario con todos sus datos (persona, cliente/empleado)
     */
    Optional<Usuario> buscarPorIdConDatosCompletos(Long id);

    /**
     * Busca usuario por email con todos sus datos
     */
    Optional<Usuario> buscarPorEmailConDatosCompletos(String email);
}