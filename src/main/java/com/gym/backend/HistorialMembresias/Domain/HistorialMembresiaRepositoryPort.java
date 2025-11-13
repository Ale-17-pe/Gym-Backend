package com.gym.backend.HistorialMembresias.Domain;

import java.util.List;

public interface HistorialMembresiaRepositoryPort {

    HistorialMembresia registrar(HistorialMembresia historial);

    List<HistorialMembresia> listarPorUsuario(Long usuarioId);

    List<HistorialMembresia> listar();

}