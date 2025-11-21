package com.gym.backend.HistorialPagos.Infrastructure.Controller;

import com.gym.backend.HistorialPagos.Application.Dto.HistorialPagoDTO;
import com.gym.backend.HistorialPagos.Application.Dto.HistorialPagoResponse;
import com.gym.backend.HistorialPagos.Application.Mapper.HistorialPagoMapper;
import com.gym.backend.HistorialPagos.Domain.HistorialPagoUseCase;
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
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/historial-pagos")
@RequiredArgsConstructor
public class HistorialPagoController {

    private final HistorialPagoUseCase useCase;
    private final HistorialPagoMapper mapper;

    @PostMapping
    public ResponseEntity<HistorialPagoResponse> registrar(@RequestBody HistorialPagoDTO dto) {
        try {
            var historial = useCase.registrarCambio(mapper.toDomain(dto));
            return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(historial));
        } catch (Exception e) {
            log.error("Error al registrar historial: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new HistorialPagoResponse(e.getMessage()));
        }
    }

    @GetMapping
    public List<HistorialPagoResponse> listar() {
        return useCase.listar().stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/paginated")
    public Page<HistorialPagoResponse> listarPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return useCase.listarPaginated(pageable).map(mapper::toResponse);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<HistorialPagoResponse> listarPorUsuario(@PathVariable Long usuarioId) {
        return useCase.listarPorUsuario(usuarioId).stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/usuario/{usuarioId}/paginated")
    public Page<HistorialPagoResponse> listarPorUsuarioPaginated(
            @PathVariable Long usuarioId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return useCase.listarPorUsuarioPaginated(usuarioId, pageable).map(mapper::toResponse);
    }

    @GetMapping("/pago/{pagoId}")
    public List<HistorialPagoResponse> listarPorPago(@PathVariable Long pagoId) {
        return useCase.listarPorPago(pagoId).stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/estado/{estado}")
    public List<HistorialPagoResponse> listarPorEstado(@PathVariable String estado) {
        return useCase.listarPorEstado(estado).stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/rango-fechas")
    public List<HistorialPagoResponse> listarPorRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return useCase.listarPorRangoFechas(inicio, fin).stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/ultimo-cambio/{pagoId}")
    public ResponseEntity<HistorialPagoResponse> obtenerUltimoCambio(@PathVariable Long pagoId) {
        var historial = useCase.obtenerUltimoCambio(pagoId);
        return historial != null ? ResponseEntity.ok(mapper.toResponse(historial)) : ResponseEntity.notFound().build();
    }

    @GetMapping("/recientes")
    public List<HistorialPagoResponse> obtenerCambiosRecientes(
            @RequestParam(defaultValue = "10") int limite) {
        return useCase.obtenerCambiosRecientes(limite).stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/timeline/{pagoId}")
    public List<Map<String, Object>> obtenerTimelinePago(@PathVariable Long pagoId) {
        return useCase.obtenerTimelinePago(pagoId);
    }

    @GetMapping("/estadisticas")
    public Map<String, Object> obtenerEstadisticas() {
        return useCase.obtenerEstadisticas();
    }

    @GetMapping("/estadisticas/mes")
    public Map<String, Long> obtenerEstadisticasPorMes(
            @RequestParam int año,
            @RequestParam int mes) {
        return useCase.obtenerEstadisticasPorMes(año, mes);
    }
}