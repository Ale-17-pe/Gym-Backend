package com.gym.backend.Membresias.Domain;

import com.gym.backend.Membresias.Domain.Enum.EstadoMembresia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MembresiaRepositoryPort {
    Membresia guardar(Membresia membresia);

    Membresia actualizar(Membresia membresia);

    Optional<Membresia> buscarPorId(Long id);

    List<Membresia> listar();

    Page<Membresia> listarPaginated(Pageable pageable);

    List<Membresia> listarPorUsuario(Long usuarioId);

    Page<Membresia> listarPorUsuarioPaginated(Long usuarioId, Pageable pageable);

    Optional<Membresia> buscarActivaPorUsuario(Long usuarioId);

    List<Membresia> listarPorEstado(EstadoMembresia estado);

    Page<Membresia> listarPorEstadoPaginated(EstadoMembresia estado, Pageable pageable);

    List<Membresia> listarPorVencer();

    List<Membresia> buscarPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin);

    Optional<Membresia> buscarPorCodigoAcceso(String codigoAcceso);

    // Métodos para estadísticas
    Long contarTotal();

    Long contarPorEstado(EstadoMembresia estado);

    Long contarPorVencer();

    Long contarPorUsuario(Long usuarioId);
}