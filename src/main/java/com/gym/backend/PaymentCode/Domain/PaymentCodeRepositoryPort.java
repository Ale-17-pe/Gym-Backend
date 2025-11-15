package com.gym.backend.PaymentCode.Domain;

import java.util.Optional;

public interface PaymentCodeRepositoryPort {
    PaymentCode guardar(PaymentCode code);
    Optional<PaymentCode> buscarPorId(Long id);
    Optional<PaymentCode> buscarPorPagoId(Long pagoId);
    Optional<PaymentCode> buscarPorCodigo(String codigo);
    PaymentCode actualizar(PaymentCode code);
}