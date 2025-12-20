package com.gym.backend.Pago.Infrastructure.Controller;

import com.gym.backend.Pago.Application.Dto.PagoDTO;
import com.gym.backend.Pago.Application.Mapper.PagoMapper;
import com.gym.backend.Pago.Domain.Pago;
import com.gym.backend.Pago.Domain.PagoUseCase;
import com.gym.backend.Usuarios.Domain.UsuarioUseCase;
import com.gym.backend.Usuarios.Domain.Usuario;
import com.gym.backend.Planes.Domain.PlanUseCase;
import com.gym.backend.Planes.Domain.Plan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {
    private final PagoUseCase useCase;
    private final PagoMapper mapper;
    private final com.gym.backend.Qr.Domain.QrUseCase qrUseCase;
    private final UsuarioUseCase usuarioUseCase;
    private final PlanUseCase planUseCase;

    private PagoDTO enrichDTO(Pago pago) {
        PagoDTO dto = mapper.toDTO(pago);

        // Obtener datos del usuario
        try {
            Usuario usuario = usuarioUseCase.obtenerConDatosCompletos(pago.getUsuarioId());
            if (usuario != null && usuario.getPersona() != null) {
                dto.setNombreUsuario(usuario.getPersona().getNombre() + " " + usuario.getPersona().getApellido());
                dto.setDniUsuario(usuario.getPersona().getDni());
                dto.setEmailUsuario(usuario.getEmail());
            }
        } catch (Exception e) {
            log.warn("No se pudo obtener info de usuario {}: {}", pago.getUsuarioId(), e.getMessage());
        }

        // Obtener datos del plan
        try {
            Plan plan = planUseCase.obtener(pago.getPlanId());
            if (plan != null) {
                dto.setNombrePlan(plan.getNombrePlan());
                dto.setPrecioPlan(plan.getPrecio());
            }
        } catch (Exception e) {
            log.warn("No se pudo obtener info de plan {}: {}", pago.getPlanId(), e.getMessage());
        }

        return dto;
    }

    @GetMapping
    public List<PagoDTO> listar() {
        return useCase.listar().stream().map(this::enrichDTO).toList();
    }

    @GetMapping("/paginated")
    public Page<PagoDTO> listarPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return useCase.listarPaginated(pageable).map(this::enrichDTO);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<PagoDTO> listarPorUsuario(@PathVariable Long usuarioId) {
        return useCase.listarPorUsuario(usuarioId).stream().map(this::enrichDTO).toList();
    }

    @GetMapping("/usuario/{usuarioId}/paginated")
    public Page<PagoDTO> listarPorUsuarioPaginated(
            @PathVariable Long usuarioId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return useCase.listarPorUsuarioPaginated(usuarioId, pageable).map(this::enrichDTO);
    }

    @GetMapping("/pendientes")
    public List<PagoDTO> listarPendientes() {
        return useCase.listarPendientes().stream().map(this::enrichDTO).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoDTO> obtener(@PathVariable Long id) {
        var pago = useCase.obtener(id);
        return ResponseEntity.ok(enrichDTO(pago));
    }

    // Nuevos endpoints para reporting
    @GetMapping("/reporte/ingresos")
    public ResponseEntity<IngresosResponse> obtenerIngresos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {

        Double ingresos = useCase.obtenerIngresosTotalesPorFecha(fechaInicio, fechaFin);
        Long totalPagos = useCase.contarPagosPorFecha(fechaInicio, fechaFin);

        return ResponseEntity.ok(new IngresosResponse(ingresos, totalPagos, fechaInicio, fechaFin));
    }

    @GetMapping("/estadisticas/mensual")
    public ResponseEntity<PagoUseCase.EstadisticasMensual> obtenerEstadisticasMensual(
            @RequestParam int año,
            @RequestParam int mes) {

        var estadisticas = useCase.obtenerEstadisticasMensual(año, mes);
        return ResponseEntity.ok(estadisticas);
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        var estadisticas = useCase.obtenerEstadisticasDiarias();
        return ResponseEntity.ok(estadisticas);
    }

    @PostMapping("/{id}/rechazar")
    public ResponseEntity<PagoDTO> rechazarPago(@PathVariable Long id) {
        var pagoRechazado = useCase.rechazar(id);
        return ResponseEntity.ok(mapper.toDTO(pagoRechazado));
    }

    // Endpoint para cancelar pagos
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<PagoDTO> cancelarPago(@PathVariable Long id) {
        var pagoCancelado = useCase.cancelar(id);
        return ResponseEntity.ok(mapper.toDTO(pagoCancelado));
    }

    @PostMapping("/generar-qr")
    public ResponseEntity<?> generarQR(@RequestBody Map<String, String> request) {
        String codigo = request.get("codigo");
        byte[] qrBytes = qrUseCase.generarQRBytes(codigo);
        String qrBase64 = Base64.getEncoder().encodeToString(qrBytes);
        return ResponseEntity.ok(Map.of("qrBase64", qrBase64));
    }

    // Records para responses
    public record IngresosResponse(Double totalIngresos, Long totalPagos,
            LocalDateTime fechaInicio, LocalDateTime fechaFin) {
    }
}
