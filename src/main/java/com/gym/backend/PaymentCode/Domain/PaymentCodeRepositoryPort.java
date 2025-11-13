package com.gym.backend.PaymentCode.Domain;

import java.util.Optional;

public interface PaymentCodeRepositoryPort {

    PaymentCode guardar(PaymentCode code);

    Optional<PaymentCode> buscarPorPago(Long pagoId);

    Optional<PaymentCode> buscarPorCodigo(String codigo);

    void actualizarEstado(Long id, String estado);
}