package com.gym.backend.PaymentCode.Domain;

import com.gym.backend.PaymentCode.Domain.Enums.EstadoPaymentCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentCodeRepositoryPort {
    PaymentCode guardar(PaymentCode code);
    PaymentCode actualizar(PaymentCode code);
    Optional<PaymentCode> buscarPorId(Long id);
    Optional<PaymentCode> buscarPorPagoId(Long pagoId);
    Optional<PaymentCode> buscarPorCodigo(String codigo);
    List<PaymentCode> listarTodos();
    Page<PaymentCode> listarPaginated(Pageable pageable);
    List<PaymentCode> listarPorEstado(EstadoPaymentCode estado);
    List<PaymentCode> listarExpirados();
    List<PaymentCode> listarExpiradosNoProcesados();
    List<PaymentCode> listarPorVencer();

    // Métodos para estadísticas
    Long contarTotal();
    Long contarPorEstado(EstadoPaymentCode estado);
    Long contarPorVencer();

    // Método para limpieza
    int eliminarAntiguos(LocalDateTime fechaLimite);
}