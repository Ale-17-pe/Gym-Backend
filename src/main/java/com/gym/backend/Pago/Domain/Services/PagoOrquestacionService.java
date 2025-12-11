package com.gym.backend.Pago.Domain.Services;

import com.gym.backend.Membresias.Domain.Enum.EstadoMembresia;
import com.gym.backend.Membresias.Domain.Membresia;
import com.gym.backend.Membresias.Domain.MembresiaUseCase;
import com.gym.backend.Notificacion.Application.NotificacionService;
import com.gym.backend.Notificacion.Domain.TipoNotificacion;
import com.gym.backend.Pago.Application.Dto.CrearPagoRequest;
import com.gym.backend.Pago.Application.Mapper.PagoMapper;
import com.gym.backend.Pago.Domain.*;
import com.gym.backend.PaymentCode.Domain.*;
import com.gym.backend.PaymentCode.Domain.PaymentCodeUseCase;
import com.gym.backend.Planes.Domain.*;
import com.gym.backend.Shared.Email.EmailService;
import com.gym.backend.Usuarios.Application.Dto.UsuarioDTO;
import com.gym.backend.Usuarios.Application.Mapper.UsuarioMapper;
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
    private final PagoMapper pagoMapper;
    private final EmailService emailService;
    private final NotificacionService notificacionService;
    private final UsuarioMapper usuarioMapper;
    private final com.gym.backend.Fidelidad.Application.PuntosFidelidadUseCase puntosFidelidadUseCase;
    private final com.gym.backend.Referidos.Application.ReferidoUseCase referidoUseCase;

    @Transactional
    public ProcesoPagoResponse iniciarProcesoPago(CrearPagoRequest request) {
        log.info("Iniciando proceso de pago para usuario: {}, plan: {}", request.getUsuarioId(), request.getPlanId());

        try {
            validarPrecondicionesPago(request);

            Pago pago = pagoMapper.toDomainFromCreateRequest(request);
            Pago pagoRegistrado = pagoUseCase.registrar(pago);

            // NOTA: El registro en historial ya se realiza en pagoUseCase.registrar()
            // No es necesario duplicar aquí

            PaymentCode paymentCode = paymentCodeUseCase.generarParaPago(pagoRegistrado.getId());
            pagoUseCase.asignarCodigo(pagoRegistrado.getId(), paymentCode.getCodigo());
            pagoRegistrado.setCodigoPago(paymentCode.getCodigo());

            log.info("Proceso de pago iniciado - Pago ID: {}, Código: {}", pagoRegistrado.getId(),
                    paymentCode.getCodigo());

            // Enviar email con código de pago
            try {
                emailService.enviarComprobantePago(pagoRegistrado.getId());
                log.info("✅ Email de pago pendiente enviado");

                // Crear notificación in-app
                var usuario = usuarioUseCase.obtener(request.getUsuarioId());
                UsuarioDTO usuarioDTO = usuarioMapper.toDTO(usuario);
                notificacionService.crearSoloInApp(
                        usuarioDTO,
                        TipoNotificacion.PAGO_PENDIENTE,
                        "Pago Pendiente Generado",
                        "Se ha generado un código de pago: " + paymentCode.getCodigo());
            } catch (Exception emailEx) {
                log.error("❌ Error enviando email/notificación: {}", emailEx.getMessage());
            }

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

            // NOTA: El registro en historial ya se realiza en pagoUseCase.confirmar()
            // No es necesario duplicar aquí

            crearOActualizarMembresia(pagoConfirmado);

            // Enviar email de felicitaciones y notificación
            try {
                emailService.enviarFelicitacionMembresia(pagoConfirmado.getId());
                log.info("✅ Email de felicitaciones enviado");

                // Crear notificación in-app
                var usuario = usuarioUseCase.obtener(pagoConfirmado.getUsuarioId());
                UsuarioDTO usuarioDTO = usuarioMapper.toDTO(usuario);
                notificacionService.crearSoloInApp(
                        usuarioDTO,
                        TipoNotificacion.PAGO_CONFIRMADO,
                        "¡Pago Confirmado!",
                        "Tu pago ha sido confirmado y tu membresía está activa.");
            } catch (Exception e) {
                log.error("❌ Error enviando email/notificación de felicitaciones: {}", e.getMessage());
            }

            // Otorgar puntos de fidelidad por la compra
            try {
                puntosFidelidadUseCase.otorgarPuntosPorCompra(
                        pagoConfirmado.getUsuarioId(),
                        pagoConfirmado.getId(),
                        pagoConfirmado.getMonto());
                log.info("🎯 Puntos otorgados por compra de S/{} al usuario: {}",
                        pagoConfirmado.getMonto(), pagoConfirmado.getUsuarioId());
            } catch (Exception e) {
                log.warn("⚠️ No se pudieron otorgar puntos por compra: {}", e.getMessage());
            }

            // Completar referido si es el primer pago del usuario
            try {
                referidoUseCase.completarReferido(pagoConfirmado.getUsuarioId(), pagoConfirmado.getId());
            } catch (Exception e) {
                log.warn("⚠️ No se pudo completar referido: {}", e.getMessage());
            }

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