package com.gym.backend.PaymentCode.Domain;

import com.gym.backend.PaymentCode.Domain.Enums.EstadoPaymentCode;
import com.gym.backend.PaymentCode.Domain.Exceptions.PaymentCodeNotFoundException;
import com.gym.backend.PaymentCode.Domain.Exceptions.PaymentCodeValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentCodeUseCase {
    private final PaymentCodeRepositoryPort repo;

    @Value("${app.payment-code.expiration-hours:24}")
    private int expiracionHoras;

    public PaymentCode generarParaPago(Long pagoId) {
        String codigo = "GYM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        LocalDateTime ahora = LocalDateTime.now();

        PaymentCode nuevo = PaymentCode.builder()
                .pagoId(pagoId)
                .codigo(codigo)
                .fechaGeneracion(ahora)
                .fechaExpiracion(ahora.plusHours(expiracionHoras))
                .estado(EstadoPaymentCode.GENERADO)
                .build();

        return repo.guardar(nuevo);
    }

    public PaymentCode validarCodigo(String codigo) {
        PaymentCode paymentCode = repo.buscarPorCodigo(codigo)
                .orElseThrow(() -> new PaymentCodeNotFoundException(codigo));

        if (paymentCode.getEstado() == EstadoPaymentCode.USADO) {
            throw new PaymentCodeValidationException("Código ya usado");
        }

        if (paymentCode.estaExpirado()) {
            paymentCode.marcarComoExpirado();
            repo.actualizar(paymentCode);
            throw new PaymentCodeValidationException("Código expirado");
        }

        return paymentCode;
    }

    public PaymentCode marcarComoUsado(Long id) {
        PaymentCode paymentCode = repo.buscarPorId(id)
                .orElseThrow(() -> new PaymentCodeNotFoundException(id.toString()));

        paymentCode.marcarComoUsado();
        return repo.actualizar(paymentCode);
    }

    public PaymentCode obtenerPorPagoId(Long pagoId) {
        return repo.buscarPorPagoId(pagoId)
                .orElseThrow(() -> new PaymentCodeNotFoundException("para pago: " + pagoId));
    }
}