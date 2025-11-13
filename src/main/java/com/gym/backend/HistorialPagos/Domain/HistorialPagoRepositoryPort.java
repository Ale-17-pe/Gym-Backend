package com.gym.backend.HistorialPagos.Domain;


import java.util.List;

public interface HistorialPagoRepositoryPort {

    HistorialPago registrar(HistorialPago historial);

    List<HistorialPago> listarPorUsuario(Long usuarioId);

    List<HistorialPago> listar();
}