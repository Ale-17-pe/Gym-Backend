package com.gym.backend.PaymentCode.Domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentCodeUseCase {

    private final PaymentCodeRepositoryPort repo;
    private final int expiracionHoras;

    public PaymentCodeUseCase(PaymentCodeRepositoryPort repo, int expiracionHoras) {
        this.repo = repo;
        this.expiracionHoras = expiracionHoras;
    }

    public PaymentCode generarParaPago(Long pagoId) {

        String codigo = "GYM-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        LocalDateTime ahora = LocalDateTime.now();

        PaymentCode nuevo = PaymentCode.builder()
                .pagoId(pagoId)
                .codigo(codigo)
                .fechaGeneracion(ahora)
                .fechaExpiracion(ahora.plusHours(expiracionHoras))
                .estado("GENERADO")
                .build();

        return repo.guardar(nuevo);
    }

    public PaymentCode validarCodigo(String codigo) {

        var op = repo.buscarPorCodigo(codigo);

        if (op.isEmpty())
            throw new IllegalStateException("Código no existe");

        PaymentCode pc = op.get();

        if (pc.getEstado().equals("USADO"))
            throw new IllegalStateException("Código ya usado");

        if (pc.getFechaExpiracion().isBefore(LocalDateTime.now()))
            throw new IllegalStateException("Código expirado");

        return pc;
    }

    public void marcarComoUsado(Long id) {
        repo.actualizarEstado(id, "USADO");
    }
}
