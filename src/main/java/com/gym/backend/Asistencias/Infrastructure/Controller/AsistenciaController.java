package com.gym.backend.Asistencias.Infrastructure.Controller;

import com.gym.backend.Asistencias.Application.Dto.AsistenciaDTO;
import com.gym.backend.Asistencias.Application.Dto.AsistenciaResponse;
import com.gym.backend.Asistencias.Application.Mapper.AsistenciaMapper;
import com.gym.backend.Asistencias.Domain.AsistenciaUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/asistencias")
@RequiredArgsConstructor
public class AsistenciaController {

    private final AsistenciaUseCase useCase;
    private final AsistenciaMapper mapper;

    @PostMapping("/entrada/{usuarioId}")
    public ResponseEntity<AsistenciaResponse> registrarEntrada(@PathVariable Long usuarioId) {
        try {
            var asistencia = useCase.registrarEntrada(usuarioId);
            return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(asistencia));
        } catch (Exception e) {
            log.error("Error al registrar entrada: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new AsistenciaResponse(e.getMessage()));
        }
    }

    @PostMapping("/salida/{usuarioId}")
    public ResponseEntity<AsistenciaResponse> registrarSalida(@PathVariable Long usuarioId) {
        try {
            var asistencia = useCase.registrarSalida(usuarioId);
            return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(asistencia));
        } catch (Exception e) {
            log.error("Error al registrar salida: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new AsistenciaResponse(e.getMessage()));
        }
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<AsistenciaResponse> cancelarAsistencia(@PathVariable Long id) {
        try {
            var asistencia = useCase.cancelarAsistencia(id);
            return ResponseEntity.ok(mapper.toResponse(asistencia));
        } catch (Exception e) {
            log.error("Error al cancelar asistencia: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new AsistenciaResponse(e.getMessage()));
        }
    }

    @GetMapping
    public List<AsistenciaResponse> listar() {
        return useCase.listar().stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/paginated")
    public Page<AsistenciaResponse> listarPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return useCase.listarPaginated(pageable).map(mapper::toResponse);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<AsistenciaResponse> listarPorUsuario(@PathVariable Long usuarioId) {
        return useCase.listarPorUsuario(usuarioId).stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/usuario/{usuarioId}/paginated")
    public Page<AsistenciaResponse> listarPorUsuarioPaginated(
            @PathVariable Long usuarioId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return useCase.listarPorUsuarioPaginated(usuarioId, pageable).map(mapper::toResponse);
    }

    @GetMapping("/fecha/{fecha}")
    public List<AsistenciaResponse> listarPorFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return useCase.listarPorFecha(fecha).stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/rango-fechas")
    public List<AsistenciaResponse> listarPorRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return useCase.listarPorRangoFechas(inicio, fin).stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/estado/{usuarioId}")
    public ResponseEntity<AsistenciaUseCase.AsistenciaEstado> obtenerEstadoUsuario(@PathVariable Long usuarioId) {
        var estado = useCase.obtenerEstadoUsuario(usuarioId);
        return ResponseEntity.ok(estado);
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        var estadisticas = useCase.obtenerEstadisticas();
        return ResponseEntity.ok(estadisticas);
    }

    @GetMapping("/estadisticas/mes")
    public ResponseEntity<Map<String, Long>> obtenerEstadisticasPorMes(
            @RequestParam int año,
            @RequestParam int mes) {
        var estadisticas = useCase.obtenerEstadisticasPorMes(año, mes);
        return ResponseEntity.ok(estadisticas);
    }

    @GetMapping("/verificar-acceso/{usuarioId}")
    public ResponseEntity<Map<String, Boolean>> verificarAcceso(@PathVariable Long usuarioId) {
        try {
            var estado = useCase.obtenerEstadoUsuario(usuarioId);
            boolean puedeAcceder = !estado.tieneEntradaHoy() || !estado.tieneSalidaHoy();
            return ResponseEntity.ok(Map.of("puedeAcceder", puedeAcceder));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("puedeAcceder", false));
        }
    }
}