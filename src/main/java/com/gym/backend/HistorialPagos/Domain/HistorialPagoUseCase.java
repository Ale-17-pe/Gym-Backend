package com.gym.backend.HistorialPagos.Domain;

import java.util.List;

public class HistorialPagoUseCase {

    private final HistorialPagoRepositoryPort repo;

    public HistorialPagoUseCase(HistorialPagoRepositoryPort repo) {
        this.repo = repo;
    }

    public HistorialPago registrar(HistorialPago historial) {
        return repo.registrar(historial);
    }

    public List<HistorialPago> listar() {
        return repo.listar();
    }

    public List<HistorialPago> listarPorUsuario(Long usuarioId) {
        return repo.listarPorUsuario(usuarioId);
    }
}