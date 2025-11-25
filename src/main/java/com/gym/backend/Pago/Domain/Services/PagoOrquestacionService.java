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
import java.time.LocalDateTime;

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
            validarPrecondicionesPago(request);

            Pago pago = pagoMapper.toDomainFromCreateRequest(request);
            Pago pagoRegistrado = pagoUseCase.registrar(pago);

            historialPago.registrarCambioAutomatico(
                    pagoRegistrado.getId(),
                    pagoRegistrado.getUsuarioId(),
                    pagoRegistrado.getPlanId(),
                    pagoRegistrado.getMonto(),
                    null,
                    "PENDIENTE",
                    "Pago iniciado");

            PaymentCode paymentCode = paymentCodeUseCase.generarParaPago(pagoRegistrado.getId());
            pagoUseCase.asignarCodigo(pagoRegistrado.getId(), paymentCode.getCodigo());
            pagoRegistrado.setCodigoPago(paymentCode.getCodigo());

            log.info("Proceso de pago iniciado - Pago ID: {}, Código: {}", pagoRegistrado.getId(),
                    paymentCode.getCodigo());

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
        var usuario = usuarioUseCase.obtener(request.getUsuarioId());
        usuarioUseCase.verificarUsuarioActivo(usuario.getId());

        Plan plan = planUseCase.obtener(request.getPlanId());
        if (!plan.esActivo()) {
            throw new IllegalStateException("El plan no está activo");
        }

        if (!request.getMonto().equals(plan.getPrecio())) {
            throw new IllegalStateException("El monto no coincide con el precio del plan");
        }
    }

    @Transactional(readOnly = true)
    public InfoPagoResponse obtenerInfoPagoPorCodigo(String codigoPago) {
        log.info("Obteniendo información del pago con código: {}", codigoPago);

        try {
            PaymentCode paymentCode = paymentCodeUseCase.validarCodigo(codigoPago);
            Pago pago = pagoUseCase.obtener(paymentCode.getPagoId());
            Plan plan = planUseCase.obtener(pago.getPlanId());
            var usuario = usuarioUseCase.obtener(pago.getUsuarioId());

            return InfoPagoResponse.builder()
                    .pagoId(pago.getId())
                    .usuarioId(pago.getUsuarioId())
                    .usuarioNombre(usuario.getNombre() + " " + usuario.getApellido())
                    .planId(pago.getPlanId())
                    .planNombre(plan.getNombrePlan())
                    .planDuracion(plan.getDuracionDias())
                    .monto(pago.getMonto())
                    .metodoPago(pago.getMetodoPago().name())
                    .estado(pago.getEstado().name())
                    .codigoPago(codigoPago)
                    .fechaCreacion(pago.getFechaCreacion())
                    .fechaVencimiento(paymentCode.getFechaExpiracion())
                    .build();

        } catch (Exception e) {
            log.error("Error al obtener información del pago: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public Pago confirmarPago(String codigoPago) {
        log.info("Confirmando pago con código: {}", codigoPago);

        try {
            PaymentCode paymentCode = paymentCodeUseCase.validarCodigo(codigoPago);
            Pago pago = pagoUseCase.obtener(paymentCode.getPagoId());
            Pago pagoConfirmado = pagoUseCase.confirmar(pago.getId());

            paymentCodeUseCase.marcarComoUsado(paymentCode.getId());

            historialPago.registrarCambioAutomatico(
                    pagoConfirmado.getId(),
                    pagoConfirmado.getUsuarioId(),
                    pagoConfirmado.getPlanId(),
                    pagoConfirmado.getMonto(),
                    pago.getEstado().name(),
                    "CONFIRMADO",
                    "Confirmación de pago");

            crearOActualizarMembresia(pagoConfirmado);

            log.info("Pago confirmado exitosamente - Pago ID: {}, Usuario: {}", pagoConfirmado.getId(),
                    pagoConfirmado.getUsuarioId());

            return pagoConfirmado;

        } catch (Exception e) {
            log.error("Error al confirmar pago: {}", e.getMessage(), e);
            throw e;
        }
    }

    private void crearOActualizarMembresia(Pago pago) {
        Plan plan = planUseCase.obtener(pago.getPlanId());
        Membresia membresiaExistente = membresiaUseCase.obtenerActivaPorUsuario(pago.getUsuarioId());

        if (membresiaExistente != null) {
            membresiaUseCase.extender(membresiaExistente.getId(), plan.getDuracionDias());
            log.info("Membresía extendida para usuario: {}", pago.getUsuarioId());
        } else {
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

    @Getter
    @Builder
    public static class InfoPagoResponse {
        private final Long pagoId;
        private final Long usuarioId;
        private final String usuarioNombre;
        private final Long planId;
        private final String planNombre;
        private final Integer planDuracion;
        private final Double monto;
        private final String metodoPago;
        private final String estado;
        private final String codigoPago;
        private final LocalDateTime fechaCreacion;
        private final LocalDateTime fechaVencimiento;
    }
}