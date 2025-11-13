package com.gym.backend.Pago.Domain;

import java.time.LocalDateTime;
import java.util.List;

public class PagoUseCase {

    private final PagoRepositoryPort repo;

    public PagoUseCase(PagoRepositoryPort repo) {
        this.repo = repo;
    }

    public Pago registrar(Pago pago) {

        if (pago.getMonto() <= 0)
            throw new IllegalStateException("El monto del pago debe ser mayor a 0.");

        Pago nuevo = Pago.builder()
                .id(null)
                .usuarioId(pago.getUsuarioId())
                .membresiaId(pago.getMembresiaId())
                .monto(pago.getMonto())
                .estado("PENDIENTE") // por defecto
                .fechaPago(LocalDateTime.now())
                .build();

        return repo.registrar(nuevo);
    }

    public Pago actualizarEstado(Long pagoId, String estado) {

        Pago pago = repo.obtenerPorId(pagoId);
        if (pago == null)
            throw new IllegalStateException("Pago no encontrado.");

        Pago actualizado = Pago.builder()
                .id(pago.getId())
                .usuarioId(pago.getUsuarioId())
                .membresiaId(pago.getMembresiaId())
                .monto(pago.getMonto())
                .fechaPago(pago.getFechaPago())
                .estado(estado)
                .build();

        return repo.actualizar(pagoId, actualizado);
    }

    public List<Pago> listar() {
        return repo.listar();
    }

    public List<Pago> porUsuario(Long usuarioId) {
        return repo.listarPorUsuario(usuarioId);
    }

    public List<Pago> porMembresia(Long membresiaId) {
        return repo.listarPorMembresia(membresiaId);
    }
}
