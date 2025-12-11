package com.gym.backend.Usuarios.Domain;

import com.gym.backend.Shared.Domain.BaseCrudPort;
import com.gym.backend.Shared.Domain.BaseListPort;
import com.gym.backend.Usuarios.Domain.Enum.Genero;
import com.gym.backend.Usuarios.Domain.Enum.Rol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Puerto del repositorio de Usuarios.
 * Extiende interfaces segregadas para cumplir con ISP:
 * - BaseCrudPort: operaciones CRUD básicas
 * - BaseListPort: operaciones de listado
 */
public interface UsuarioRepositoryPort extends BaseCrudPort<Usuario, Long>, BaseListPort<Usuario> {

    // ==================== BÚSQUEDAS ESPECÍFICAS ====================

    Optional<Usuario> buscarPorEmail(String email);

    Optional<Usuario> buscarPorDni(String dni);

    // ==================== LISTADOS POR FILTRO ====================

    List<Usuario> listarPorRol(Rol rol);

    Page<Usuario> listarPorRolPaginated(Rol rol, Pageable pageable);

    List<Usuario> listarPorGenero(Genero genero);

    List<Usuario> listarPorActivo(Boolean activo);

    // ==================== OPERACIONES DE ELIMINACIÓN ====================

    void eliminar(Long id);
}