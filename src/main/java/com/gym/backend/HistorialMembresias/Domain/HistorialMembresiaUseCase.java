package com.gym.backend.HistorialMembresias.Domain;

import java.util.List;

public class HistorialMembresiaUseCase {

    private final HistorialMembresiaRepositoryPort repo;

    public HistorialMembresiaUseCase(HistorialMembresiaRepositoryPort repo) {
        this.repo = repo;
    }

    public HistorialMembresia registrar(HistorialMembresia historial) {
        return repo.registrar(historial);
    }

    public List<HistorialMembresia> listar() {
        return repo.listar();
    }

    public List<HistorialMembresia> listarPorUsuario(Long usuarioId) {
        return repo.listarPorUsuario(usuarioId);
    }
}