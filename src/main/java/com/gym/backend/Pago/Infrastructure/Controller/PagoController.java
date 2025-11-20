package com.gym.backend.Pago.Infrastructure.Controller;

import com.gym.backend.Pago.Application.Dto.PagoDTO;
import com.gym.backend.Pago.Application.Mapper.PagoMapper;
import com.gym.backend.Pago.Domain.PagoUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {
    private final PagoUseCase useCase;
    private final PagoMapper mapper;

    @GetMapping
    public List<PagoDTO> listar() {
        return useCase.listar().stream().map(mapper::toDTO).toList();
    }

    @GetMapping("/paginated")
    public Page<PagoDTO> listarPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return useCase.listarPaginated(pageable).map(mapper::toDTO);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<PagoDTO> listarPorUsuario(@PathVariable Long usuarioId) {
        return useCase.listarPorUsuario(usuarioId).stream().map(mapper::toDTO).toList();
    }

    @GetMapping("/usuario/{usuarioId}/paginated")
    public Page<PagoDTO> listarPorUsuarioPaginated(
            @PathVariable Long usuarioId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return useCase.listarPorUsuarioPaginated(usuarioId, pageable).map(mapper::toDTO);
    }

    @GetMapping("/pendientes")
    public List<PagoDTO> listarPendientes() {
        return useCase.listarPendientes().stream().map(mapper::toDTO).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoDTO> obtener(@PathVariable Long id) {
        var pago = useCase.obtener(id);
        return ResponseEntity.ok(mapper.toDTO(pago));
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

    // Endpoint para rechazar pagos
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

    // Records para responses
    public record IngresosResponse(Double totalIngresos, Long totalPagos,
                                   LocalDateTime fechaInicio, LocalDateTime fechaFin) {}
}