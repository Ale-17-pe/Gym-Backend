package com.gym.backend.HistorialMembresias.Infrastructure.Controller;

import com.gym.backend.HistorialMembresias.Application.Dto.HistorialMembresiaDTO;
import com.gym.backend.HistorialMembresias.Application.Dto.HistorialMembresiaResponse;
import com.gym.backend.HistorialMembresias.Application.Mapper.HistorialMembresiaMapper;
import com.gym.backend.HistorialMembresias.Domain.HistorialMembresiaUseCase;
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
@RequestMapping("/api/historial-membresias")
@RequiredArgsConstructor
public class HistorialMembresiaController {

    private final HistorialMembresiaUseCase useCase;
    private final HistorialMembresiaMapper mapper;

    @PostMapping
    public ResponseEntity<HistorialMembresiaResponse> registrar(@RequestBody HistorialMembresiaDTO dto) {
        try {
            var historial = useCase.registrarCambio(mapper.toDomain(dto));
            return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(historial));
        } catch (Exception e) {
            log.error("Error al registrar historial: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new HistorialMembresiaResponse(e.getMessage()));
        }
    }

    @GetMapping
    public List<HistorialMembresiaResponse> listar() {
        return useCase.listar().stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/paginated")
    public Page<HistorialMembresiaResponse> listarPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return useCase.listarPaginated(pageable).map(mapper::toResponse);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<HistorialMembresiaResponse> listarPorUsuario(@PathVariable Long usuarioId) {
        return useCase.listarPorUsuario(usuarioId).stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/usuario/{usuarioId}/paginated")
    public Page<HistorialMembresiaResponse> listarPorUsuarioPaginated(
            @PathVariable Long usuarioId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return useCase.listarPorUsuarioPaginated(usuarioId, pageable).map(mapper::toResponse);
    }

    @GetMapping("/membresia/{membresiaId}")
    public List<HistorialMembresiaResponse> listarPorMembresia(@PathVariable Long membresiaId) {
        return useCase.listarPorMembresia(membresiaId).stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/accion/{accion}")
    public List<HistorialMembresiaResponse> listarPorAccion(@PathVariable String accion) {
        return useCase.listarPorAccion(accion).stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/rango-fechas")
    public List<HistorialMembresiaResponse> listarPorRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return useCase.listarPorRangoFechas(inicio, fin).stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/ultimo-cambio/{membresiaId}")
    public ResponseEntity<HistorialMembresiaResponse> obtenerUltimoCambio(@PathVariable Long membresiaId) {
        var historial = useCase.obtenerUltimoCambio(membresiaId);
        return historial != null ?
                ResponseEntity.ok(mapper.toResponse(historial)) :
                ResponseEntity.notFound().build();
    }

    @GetMapping("/recientes")
    public List<HistorialMembresiaResponse> obtenerCambiosRecientes(
            @RequestParam(defaultValue = "10") int limite) {
        return useCase.obtenerCambiosRecientes(limite).stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/timeline/{membresiaId}")
    public List<Map<String, Object>> obtenerTimelineMembresia(@PathVariable Long membresiaId) {
        return useCase.obtenerTimelineMembresia(membresiaId);
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