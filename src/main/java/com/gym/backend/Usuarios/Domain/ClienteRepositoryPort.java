package com.gym.backend.Usuarios.Domain;

import com.gym.backend.Usuarios.Domain.Enum.NivelExperiencia;
import com.gym.backend.Usuarios.Domain.Enum.ObjetivoFitness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Puerto del repositorio de Cliente
 */
public interface ClienteRepositoryPort {

    Cliente guardar(Cliente cliente);

    Optional<Cliente> buscarPorId(Long id);

    Optional<Cliente> buscarPorUsuarioId(Long usuarioId);

    Optional<Cliente> buscarPorPersonaId(Long personaId);

    List<Cliente> listar();

    List<Cliente> listarActivos();

    Page<Cliente> listarActivosPaginado(Pageable pageable);

    List<Cliente> buscarPorObjetivo(ObjetivoFitness objetivo);

    List<Cliente> buscarPorNivel(NivelExperiencia nivel);

    List<Cliente> buscarPorCodigoReferido(String codigoReferido);

    long contarActivos();

    void eliminar(Long id);
}
