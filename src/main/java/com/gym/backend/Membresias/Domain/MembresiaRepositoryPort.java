package com.gym.backend.Membresias.Domain;

import com.gym.backend.Membresias.Domain.Enum.EstadoMembresia;

import java.util.List;
import java.util.Optional;

public interface MembresiaRepositoryPort {
    Membresia guardar(Membresia membresia);
    Membresia actualizar(Membresia membresia);
    Optional<Membresia> buscarPorId(Long id);
    List<Membresia> listar();
    List<Membresia> listarPorUsuario(Long usuarioId);
    Optional<Membresia> buscarActivaPorUsuario(Long usuarioId);
    List<Membresia> listarPorEstado(EstadoMembresia estado);
}