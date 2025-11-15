package com.gym.backend.Pago.Domain;

import com.gym.backend.Pago.Domain.Enum.EstadoPago;
import com.gym.backend.Pago.Domain.Exceptions.PagoNotFoundException;
import com.gym.backend.Pago.Domain.Exceptions.PagoValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PagoUseCase {
    private final PagoRepositoryPort repo;

    public Pago registrar(Pago pago) {
        pago.validar();
        Pago pagoParaGuardar = Pago.builder()
                .id(pago.getId())
                .usuarioId(pago.getUsuarioId())
                .planId(pago.getPlanId())
                .monto(pago.getMonto())
                .estado(EstadoPago.PENDIENTE)
                .metodoPago(pago.getMetodoPago())
                .referencia(pago.getReferencia())
                .fechaCreacion(LocalDateTime.now())
                .fechaPago(null)
                .fechaActualizacion(LocalDateTime.now())
                .build();
        return repo.guardar(pagoParaGuardar);
    }

    public Pago confirmar(Long pagoId) {
        Pago pago = obtener(pagoId);
        if (!pago.puedeSerConfirmado()) throw new PagoValidationException("El pago no puede ser confirmado");
        pago.confirmar();
        return repo.actualizar(Pago.builder()
                .id(pago.getId())
                .usuarioId(pago.getUsuarioId())
                .planId(pago.getPlanId())
                .monto(pago.getMonto())
                .estado(pago.getEstado())
                .metodoPago(pago.getMetodoPago())
                .referencia(pago.getReferencia())
                .fechaCreacion(pago.getFechaCreacion())
                .fechaPago(pago.getFechaPago())
                .fechaActualizacion(LocalDateTime.now())
                .build());
    }

    public Pago rechazar(Long pagoId) {
        Pago pago = obtener(pagoId);
        pago.rechazar();
        return repo.actualizar(Pago.builder()
                .id(pago.getId())
                .usuarioId(pago.getUsuarioId())
                .planId(pago.getPlanId())
                .monto(pago.getMonto())
                .estado(pago.getEstado())
                .metodoPago(pago.getMetodoPago())
                .referencia(pago.getReferencia())
                .fechaCreacion(pago.getFechaCreacion())
                .fechaPago(pago.getFechaPago())
                .fechaActualizacion(LocalDateTime.now())
                .build());
    }

    public Pago obtener(Long id) {
        return repo.buscarPorId(id).orElseThrow(() -> new PagoNotFoundException(id));
    }

    public List<Pago> listar() { return repo.listar(); }
    public List<Pago> listarPorUsuario(Long usuarioId) { return repo.listarPorUsuario(usuarioId); }
    public List<Pago> listarPendientes() { return repo.listarPagosPendientes(); }
}
