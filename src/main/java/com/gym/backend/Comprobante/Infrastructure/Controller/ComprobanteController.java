package com.gym.backend.Comprobante.Infrastructure.Controller;

import com.gym.backend.Comprobante.Application.Dto.ComprobanteResponse;
import com.gym.backend.Comprobante.Application.Mapper.ComprobanteMapper;
import com.gym.backend.Comprobante.Domain.Comprobante;
import com.gym.backend.Comprobante.Domain.ComprobanteUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestión de comprobantes de pago
 */
@Slf4j
@RestController
@RequestMapping("/api/comprobantes")
@RequiredArgsConstructor
public class ComprobanteController {

    private final ComprobanteUseCase useCase;
    private final ComprobanteMapper mapper;

    /**
     * Genera un comprobante para un pago confirmado
     * POST /api/comprobantes/generar/{pagoId}
     */
    @PostMapping("/generar/{pagoId}")
    public ResponseEntity<?> generarComprobante(@PathVariable Long pagoId) {
        try {
            // Obtener ID del usuario actual (recepcionista/admin)
            Long usuarioEmisorId = obtenerUsuarioActualId();

            Comprobante comprobante = useCase.generarComprobante(pagoId, usuarioEmisorId);
            ComprobanteResponse response = mapper.toResponse(comprobante);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("Error al generar comprobante para pago {}: {}", pagoId, e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Descarga el PDF de un comprobante
     * GET /api/comprobantes/{id}/pdf
     */
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> descargarPDF(@PathVariable Long id) {
        try {
            byte[] pdfData = useCase.obtenerPDF(id);
            Comprobante comprobante = useCase.obtener(id);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    "comprobante-" + comprobante.getNumeroComprobante() + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfData, headers, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error al descargar PDF del comprobante {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Obtiene el comprobante asociado a un pago
     * GET /api/comprobantes/pago/{pagoId}
     */
    @GetMapping("/pago/{pagoId}")
    public ResponseEntity<?> obtenerPorPago(@PathVariable Long pagoId) {
        try {
            Comprobante comprobante = useCase.obtenerPorPagoId(pagoId);
            return ResponseEntity.ok(mapper.toResponse(comprobante));

        } catch (Exception e) {
            log.error("Error al obtener comprobante del pago {}: {}", pagoId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Lista todos los comprobantes de un usuario
     * GET /api/comprobantes/usuario/{usuarioId}
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ComprobanteResponse>> listarPorUsuario(@PathVariable Long usuarioId) {
        try {
            List<Comprobante> comprobantes = useCase.listarPorUsuario(usuarioId);
            List<ComprobanteResponse> responses = comprobantes.stream()
                    .map(mapper::toResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responses);

        } catch (Exception e) {
            log.error("Error al listar comprobantes del usuario {}: {}", usuarioId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Lista todos los comprobantes (solo admin)
     * GET /api/comprobantes
     */
    @GetMapping
    public ResponseEntity<List<ComprobanteResponse>> listar() {
        try {
            List<Comprobante> comprobantes = useCase.listar();
            List<ComprobanteResponse> responses = comprobantes.stream()
                    .map(mapper::toResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responses);

        } catch (Exception e) {
            log.error("Error al listar comprobantes: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene un comprobante por ID (detalles sin PDF)
     * GET /api/comprobantes/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        try {
            Comprobante comprobante = useCase.obtener(id);
            return ResponseEntity.ok(mapper.toResponse(comprobante));

        } catch (Exception e) {
            log.error("Error al obtener comprobante {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Obtiene el ID del usuario autenticado actualmente
     */
    private Long obtenerUsuarioActualId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication
                    .getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
                // TODO: Obtener ID del usuario a partir del username
                return 1L; // Fallback temporal
            }
            return 1L; // Fallback
        } catch (Exception e) {
            log.warn("No se pudo obtener el usuario actual, usando ID por defecto");
            return 1L;
        }
    }

    public record ErrorResponse(String message) {
    }
}
