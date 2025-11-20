package com.gym.backend.Pago.Domain.Services;

import com.gym.backend.HistorialPagos.Domain.HistorialPagoUseCase;
import com.gym.backend.Membresias.Domain.Enum.EstadoMembresia;
import com.gym.backend.Membresias.Domain.Membresia;
import com.gym.backend.Membresias.Domain.MembresiaUseCase;
import com.gym.backend.Pago.Application.Dto.CrearPagoRequest;
import com.gym.backend.Pago.Application.Mapper.PagoMapper;
import com.gym.backend.Pago.Domain.Pago;
import com.gym.backend.Pago.Domain.PagoUseCase;
import com.gym.backend.PaymentCode.Domain.PaymentCode;
import com.gym.backend.PaymentCode.Domain.PaymentCodeUseCase;
import com.gym.backend.Planes.Domain.Plan;
import com.gym.backend.Planes.Domain.PlanUseCase;
import com.gym.backend.Usuarios.Domain.UsuarioUseCase;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PagoOrquestacionService {
    private final PagoUseCase pagoUseCase;
    private final PaymentCodeUseCase paymentCodeUseCase;
    private final MembresiaUseCase membresiaUseCase;
    private final PlanUseCase planUseCase;
    private final UsuarioUseCase usuarioUseCase;
    private final HistorialPagoUseCase historialPago;
    private final PagoMapper pagoMapper;

    @Transactional
    public ProcesoPagoResponse iniciarProcesoPago(CrearPagoRequest request) {
        log.info("Iniciando proceso de pago para usuario: {}, plan: {}", request.getUsuarioId(), request.getPlanId());

        try {
            // Validaciones más robustas
            validarPrecondicionesPago(request);

            // Crear pago
            Pago pago = pagoMapper.toDomainFromCreateRequest(request);
            Pago pagoRegistrado = pagoUseCase.registrar(pago);

            historialPago.registrarCambioAutomatico(
                    pagoRegistrado.getId(),
                    pagoRegistrado.getUsuarioId(),
                    pagoRegistrado.getPlanId(),
                    pagoRegistrado.getMonto(),
                    null,
                    "PENDIENTE",
                    "Pago iniciado"
            );

            // Generar código de pago
            PaymentCode paymentCode = paymentCodeUseCase.generarParaPago(pagoRegistrado.getId());

            log.info("Proceso de pago iniciado - Pago ID: {}, Código: {}", pagoRegistrado.getId(), paymentCode.getCodigo());

            return ProcesoPagoResponse.builder()
                    .pago(pagoRegistrado)
                    .paymentCode(paymentCode)
                    .build();

        } catch (Exception e) {
            log.error("Error al iniciar proceso de pago: {}", e.getMessage(), e);
            throw e;
        }
    }

    private void validarPrecondicionesPago(CrearPagoRequest request) {
        // Validar que el usuario existe y está activo
        var usuario = usuarioUseCase.obtener(request.getUsuarioId());
        usuarioUseCase.verificarUsuarioActivo(usuario.getId());

        // Validar que el plan existe y está activo
        Plan plan = planUseCase.obtener(request.getPlanId());
        if (!plan.esActivo()) {throw new IllegalStateException("El plan no está activo");}

        // Validar que el monto coincide con el precio del plan
        if (!request.getMonto().equals(plan.getPrecio())) {
            throw new IllegalStateException("El monto no coincide con el precio del plan");}
    }

    @Transactional
    public Pago confirmarPago(String codigoPago) {
        log.info("Confirmando pago con código: {}", codigoPago);

        try {
            // Validar código
            PaymentCode paymentCode = paymentCodeUseCase.validarCodigo(codigoPago);

            // Obtener y confirmar pago
            Pago pago = pagoUseCase.obtener(paymentCode.getPagoId());
            Pago pagoConfirmado = pagoUseCase.confirmar(pago.getId());

            // Marcar código como usado
            paymentCodeUseCase.marcarComoUsado(paymentCode.getId());

            historialPago.registrarCambioAutomatico(
                    pagoConfirmado.getId(),
                    pagoConfirmado.getUsuarioId(),
                    pagoConfirmado.getPlanId(),
                    pagoConfirmado.getMonto(),
                    pago.getEstado().name(),
                    "CONFIRMADO",
                    "Confirmación de pago"
            );

            // Crear/actualizar membresía
            crearOActualizarMembresia(pagoConfirmado);

            log.info("Pago confirmado exitosamente - Pago ID: {}, Usuario: {}", pagoConfirmado.getId(), pagoConfirmado.getUsuarioId());

            return pagoConfirmado;

        } catch (Exception e) {
            log.error("Error al confirmar pago: {}", e.getMessage(), e);
            throw e;
        }
    }

    private void crearOActualizarMembresia(Pago pago) {
        Plan plan = planUseCase.obtener(pago.getPlanId());

        // Verificar membresía existente
        Membresia membresiaExistente = membresiaUseCase.obtenerActivaPorUsuario(pago.getUsuarioId());

        if (membresiaExistente != null) {
            // Extender membresía existente
            membresiaUseCase.extender(membresiaExistente.getId(), plan.getDuracionDias());

            log.info("Membresía extendida para usuario: {}", pago.getUsuarioId());
        } else {
            // Crear nueva membresía
            Membresia nuevaMembresia = Membresia.builder()
                    .usuarioId(pago.getUsuarioId())
                    .planId(pago.getPlanId())
                    .pagoId(pago.getId())
                    .fechaInicio(LocalDate.now())
                    .fechaFin(LocalDate.now().plusDays(plan.getDuracionDias()))
                    .estado(EstadoMembresia.ACTIVA)
                    .fechaCreacion(LocalDate.now())
                    .fechaActualizacion(LocalDate.now())
                    .build();

            membresiaUseCase.crear(nuevaMembresia);
            log.info("Nueva membresía creada para usuario: {}", pago.getUsuarioId());
        }
    }

    @Getter
    @Builder
    public static class ProcesoPagoResponse {
        private final Pago pago;
        private final PaymentCode paymentCode;
    }
}