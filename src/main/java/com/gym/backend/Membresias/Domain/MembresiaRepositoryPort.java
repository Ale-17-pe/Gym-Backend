package com.gym.backend.Membresias.Domain;

import java.util.List;

public interface MembresiaRepositoryPort {

    Membresia crear(Membresia membresia);

    Membresia actualizar(Long id, Membresia membresia);

    Membresia obtenerPorId(Long id);

    List<Membresia> listar();

    List<Membresia> buscarPorUsuario(Long usuarioId);

    Membresia buscarActivaPorUsuario(Long usuarioId);

    void eliminar(Long id);
}