package com.gym.backend.PaymentCode.Domain;

import com.gym.backend.PaymentCode.Domain.Enums.EstadoPaymentCode;
import com.gym.backend.PaymentCode.Domain.Exceptions.PaymentCodeNotFoundException;
import com.gym.backend.PaymentCode.Domain.Exceptions.PaymentCodeValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentCodeUseCase {
    private final PaymentCodeRepositoryPort repo;

    @Value("${app.payment-code.expiration-hours}")
    private int expiracionHoras;

    public PaymentCode generarParaPago(Long pagoId) {
        log.info("Generando código de pago para pagoId: {}", pagoId);

        // Verificar si ya existe un código para este pago
        repo.buscarPorPagoId(pagoId).ifPresent(existente -> {
            throw new PaymentCodeValidationException("Ya existe un código de pago para este pago");
        });

        String codigo = generarCodigoUnico();
        LocalDateTime ahora = LocalDateTime.now();

        PaymentCode nuevo = PaymentCode.builder()
                .pagoId(pagoId)
                .codigo(codigo)
                .fechaGeneracion(ahora)
                .fechaExpiracion(ahora.plusHours(expiracionHoras))
                .estado(EstadoPaymentCode.GENERADO)
                .fechaCreacion(ahora)
                .fechaActualizacion(ahora)
                .build();

        nuevo.validar();
        PaymentCode guardado = repo.guardar(nuevo);

        log.info("Código de pago generado: {} para pagoId: {}", guardado.getCodigo(), pagoId);
        return guardado;
    }

    @Transactional(readOnly = true)
    public PaymentCode validarCodigo(String codigo) {
        log.info("Validando código: {}", codigo);

        PaymentCode paymentCode = repo.buscarPorCodigo(codigo)
                .orElseThrow(() -> new PaymentCodeNotFoundException(codigo));

        if (paymentCode.getEstado() == EstadoPaymentCode.USADO) {
            throw new PaymentCodeValidationException("Código ya ha sido utilizado");
        }

        if (paymentCode.getEstado() == EstadoPaymentCode.CANCELADO) {
            throw new PaymentCodeValidationException("Código ha sido cancelado");
        }

        if (paymentCode.estaExpirado()) {
            paymentCode.marcarComoExpirado();
            repo.actualizar(paymentCode);
            throw new PaymentCodeValidationException("Código ha expirado");
        }

        log.info("Código validado exitosamente: {}", codigo);
        return paymentCode;
    }

    public PaymentCode marcarComoUsado(Long id) {
        log.info("Marcando código como usado, ID: {}", id);

        PaymentCode paymentCode = repo.buscarPorId(id)
                .orElseThrow(() -> new PaymentCodeNotFoundException(id.toString()));

        if (!paymentCode.puedeSerUsado()) {
            throw new PaymentCodeValidationException("El código no puede ser marcado como usado");
        }

        paymentCode.marcarComoUsado();
        PaymentCode actualizado = repo.actualizar(paymentCode);

        log.info("Código marcado como usado: {}", actualizado.getCodigo());
        return actualizado;
    }

    public PaymentCode cancelarCodigo(Long id) {
        log.info("Cancelando código, ID: {}", id);

        PaymentCode paymentCode = repo.buscarPorId(id)
                .orElseThrow(() -> new PaymentCodeNotFoundException(id.toString()));

        paymentCode.cancelar();
        PaymentCode actualizado = repo.actualizar(paymentCode);

        log.info("Código cancelado: {}", actualizado.getCodigo());
        return actualizado;
    }

    @Transactional(readOnly = true)
    public PaymentCode obtenerPorPagoId(Long pagoId) {
        return repo.buscarPorPagoId(pagoId)
                .orElseThrow(() -> new PaymentCodeNotFoundException("para pago: " + pagoId));
    }

    @Transactional(readOnly = true)
    public PaymentCode obtenerPorId(Long id) {
        return repo.buscarPorId(id)
                .orElseThrow(() -> new PaymentCodeNotFoundException(id.toString()));
    }

    @Transactional(readOnly = true)
    public List<PaymentCode> listarTodos() {
        return repo.listarTodos();
    }

    @Transactional(readOnly = true)
    public Page<PaymentCode> listarPaginated(Pageable pageable) {
        return repo.listarPaginated(pageable);
    }

    @Transactional(readOnly = true)
    public List<PaymentCode> listarPorEstado(EstadoPaymentCode estado) {
        return repo.listarPorEstado(estado);
    }

    @Transactional(readOnly = true)
    public List<PaymentCode> listarExpirados() {
        return repo.listarExpirados();
    }

    @Transactional(readOnly = true)
    public List<PaymentCode> listarPorVencer() {
        return repo.listarPorVencer();
    }

    @Transactional(readOnly = true)
    public List<PaymentCode> listarActivos() {
        return repo.listarPorEstado(EstadoPaymentCode.GENERADO);
    }

    @Transactional(readOnly = true)
    public EstadisticasPaymentCode obtenerEstadisticas() {
        Long total = repo.contarTotal();
        Long generados = repo.contarPorEstado(EstadoPaymentCode.GENERADO);
        Long usados = repo.contarPorEstado(EstadoPaymentCode.USADO);
        Long expirados = repo.contarPorEstado(EstadoPaymentCode.EXPIRADO);
        Long cancelados = repo.contarPorEstado(EstadoPaymentCode.CANCELADO);
        Long porVencer = repo.contarPorVencer();

        return new EstadisticasPaymentCode(total, generados, usados, expirados, cancelados, porVencer);
    }

    @Scheduled(cron = "0 */5 * * * *") // Ejecutar cada 5 minutos
    @Transactional
    public void expirarCodigosAutomaticamente() {
        log.info("Ejecutando tarea programada para expirar códigos...");

        List<PaymentCode> expirados = repo.listarExpiradosNoProcesados();
        int contador = 0;

        for (PaymentCode code : expirados) {
            code.marcarComoExpirado();
            repo.actualizar(code);
            contador++;
            log.debug("Código expirado automáticamente: {}", code.getCodigo());
        }

        log.info("Tarea completada: {} códigos expirados automáticamente", contador);
    }

    // Tarea programada para limpiar códigos muy antiguos
    @Scheduled(cron = "0 0 2 * * ?") // Ejecutar todos los días a las 2 AM
    @Transactional
    public void limpiarCodigosAntiguos() {
        log.info("Ejecutando limpieza de códigos antiguos...");

        LocalDateTime fechaLimite = LocalDateTime.now().minusDays(30); // 30 días
        int eliminados = repo.eliminarAntiguos(fechaLimite);

        log.info("Limpieza completada: {} códigos eliminados", eliminados);
    }

    private String generarCodigoUnico() {
        String codigo;
        int intentos = 0;
        int maxIntentos = 5;

        do {
            codigo = "GYM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            intentos++;
        } while (repo.buscarPorCodigo(codigo).isPresent() && intentos < maxIntentos);

        if (intentos >= maxIntentos) {
            throw new PaymentCodeValidationException(
                    "No se pudo generar un código único después de " + maxIntentos + " intentos");
        }

        return codigo;
    }

    public record EstadisticasPaymentCode(Long total, Long generados, Long usados,
            Long expirados, Long cancelados, Long porVencer) {
    }
}