package com.gym.backend.Comprobante.Domain;

import com.gym.backend.Comprobante.Application.Dto.DatosComprobante;
import com.gym.backend.Comprobante.Domain.Exceptions.ComprobanteDuplicadoException;
import com.gym.backend.Comprobante.Domain.Exceptions.ComprobanteNotFoundException;
import com.gym.backend.Comprobante.Domain.Services.ComprobantePdfService;
import com.gym.backend.Membresias.Domain.Membresia;
import com.gym.backend.Membresias.Domain.MembresiaUseCase;
import com.gym.backend.Pago.Domain.Pago;
import com.gym.backend.Pago.Domain.PagoUseCase;
import com.gym.backend.Planes.Domain.Plan;
import com.gym.backend.Planes.Domain.PlanUseCase;
import com.gym.backend.Usuarios.Domain.Usuario;
import com.gym.backend.Usuarios.Domain.UsuarioUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Caso de uso principal para la gestión de comprobantes de pago
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ComprobanteUseCase {

    private final ComprobanteRepositoryPort repository;
    private final ComprobantePdfService pdfService;
    private final PagoUseCase pagoUseCase;
    private final UsuarioUseCase usuarioUseCase;
    private final MembresiaUseCase membresiaUseCase;
    private final PlanUseCase planUseCase;

    /**
     * Genera un comprobante de pago
     *
     * @param pagoId          ID del pago confirmado
     * @param usuarioEmisorId ID del usuario que genera el comprobante
     *                        (recepcionista)
     * @return Comprobante generado
     */
    public Comprobante generarComprobante(Long pagoId, Long usuarioEmisorId) {
        log.info("Generando comprobante para pago ID: {}", pagoId);

        // Verificar que no exista ya un comprobante para este pago
        if (repository.existePorPagoId(pagoId)) {
            throw new ComprobanteDuplicadoException(pagoId);
        }

        // Obtener datos del pago
        Pago pago = pagoUseCase.obtener(pagoId);

        // Obtener datos del cliente (usuario)
        Usuario cliente = usuarioUseCase.obtener(pago.getUsuarioId());

        // Obtener datos del plan
        Plan plan = planUseCase.obtener(pago.getPlanId());

        // Obtener membresía activa del usuario para obtener fechas
        Membresia membresia = membresiaUseCase.obtenerActivaPorUsuario(pago.getUsuarioId());

        // Generar número de comprobante consecutivo
        String numeroComprobante = generarNumeroComprobante();

        // Calcular desglose (asumiendo que el monto ya incluye IGV)
        BigDecimal totalPago = BigDecimal.valueOf(pago.getMonto());
        BigDecimal divisor = new BigDecimal("1.18");
        BigDecimal subtotal = totalPago.divide(divisor, 2, java.math.RoundingMode.HALF_UP); // Subtotal sin IGV
        BigDecimal igv = totalPago.subtract(subtotal); // IGV (18%)
        BigDecimal descuento = BigDecimal.ZERO; // TODO: Obtener descuento si existe

        // Preparar datos para el PDF
        DatosComprobante datos = DatosComprobante.builder()
                // Información del Gimnasio (TODO: Obtener de configuración)
                .nombreGimnasio("GIMNASIO FITNESS CENTER")
                .direccionGimnasio("Av. Principal #123, Lima")
                .rucGimnasio("20123456789")
                .telefonoGimnasio("(01) 555-1234")
                // Información del comprobante
                .numeroComprobante(numeroComprobante)
                .fechaEmision(LocalDateTime.now())
                // Información del cliente
                .nombreCliente(cliente.getNombreCompleto())
                .documentoCliente(cliente.getDni())
                .usuarioId(cliente.getId())
                // Información de la membresía
                .conceptoPago(plan.getNombrePlan())
                .fechaInicio(membresia.getFechaInicio().atStartOfDay())
                .fechaFin(membresia.getFechaFin().atStartOfDay())
                // Desglose de costos
                .subtotal(subtotal)
                .descuento(descuento)
                .igv(igv)
                .total(totalPago)
                // Información del pago
                .metodoPago(pago.getMetodoPago().name())
                .codigoOperacion(pago.getReferencia())
                .codigoPago(pago.getCodigoPago())
                // Auditoría
                .emitidoPor(obtenerNombreUsuarioEmisor(usuarioEmisorId))
                .pagoId(pagoId)
                .build();

        // Generar PDF
        byte[] pdfData = pdfService.generarComprobantePDF(datos);

        // Crear y guardar comprobante
        Comprobante comprobante = Comprobante.builder()
                .numeroComprobante(numeroComprobante)
                .pagoId(pagoId)
                .usuarioId(cliente.getId())
                .fechaEmision(datos.getFechaEmision())
                .subtotal(subtotal)
                .igv(igv)
                .total(totalPago)
                .descuento(descuento)
                .pdfData(pdfData)
                .emitidoPor(datos.getEmitidoPor())
                .build();

        Comprobante guardado = repository.guardar(comprobante);
        log.info("Comprobante generado exitosamente: {}", numeroComprobante);

        return guardado;
    }

    /**
     * Obtiene un comprobante por su ID
     */
    @Transactional(readOnly = true)
    public Comprobante obtener(Long id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new ComprobanteNotFoundException(id));
    }

    /**
     * Obtiene el comprobante asociado a un pago
     */
    @Transactional(readOnly = true)
    public Comprobante obtenerPorPagoId(Long pagoId) {
        return repository.buscarPorPagoId(pagoId)
                .orElseThrow(() -> new ComprobanteNotFoundException(
                        "No se encontró comprobante para el pago ID: " + pagoId));
    }

    /**
     * Lista todos los comprobantes de un usuario
     */
    @Transactional(readOnly = true)
    public List<Comprobante> listarPorUsuario(Long usuarioId) {
        return repository.listarPorUsuario(usuarioId);
    }

    /**
     * Lista todos los comprobantes
     */
    @Transactional(readOnly = true)
    public List<Comprobante> listar() {
        return repository.listar();
    }

    /**
     * Obtiene solo el PDF de un comprobante
     */
    @Transactional(readOnly = true)
    public byte[] obtenerPDF(Long comprobanteId) {
        Comprobante comprobante = obtener(comprobanteId);
        if (comprobante.getPdfData() == null || comprobante.getPdfData().length == 0) {
            throw new ComprobanteNotFoundException("El comprobante no tiene PDF asociado");
        }
        return comprobante.getPdfData();
    }

    /**
     * Genera un número de comprobante consecutivo
     * Formato: COMP-00001, COMP-00002, etc.
     */
    private String generarNumeroComprobante() {
        // TODO: Implementar secuencia en base de datos para garantizar unicidad
        // Por ahora, usar timestamp como fallback
        long timestamp = System.currentTimeMillis();
        long numero = timestamp % 1000000; // Últimos 6 dígitos
        return String.format("COMP-%06d", numero);
    }

    /**
     * Obtiene el nombre del usuario que emite el comprobante
     */
    private String obtenerNombreUsuarioEmisor(Long usuarioId) {
        try {
            Usuario emisor = usuarioUseCase.obtener(usuarioId);
            return emisor.getNombreCompleto();
        } catch (Exception e) {
            log.warn("No se pudo obtener el nombre del usuario emisor: {}", usuarioId);
            return "Sistema";
        }
    }
}
