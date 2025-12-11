package com.gym.backend.Rutinas.Infrastructure.Controller;

import com.gym.backend.Rutinas.Application.ProgresoEntrenamientoUseCase;
import com.gym.backend.Rutinas.Application.ProgresoEntrenamientoUseCase.*;
import com.gym.backend.Rutinas.Domain.RegistroEntrenamiento;
import com.gym.backend.Shared.Security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/entrenamientos")
@RequiredArgsConstructor
@Tag(name = "Progreso de Entrenamiento", description = "Registro y seguimiento de entrenamientos")
@SecurityRequirement(name = "bearerAuth")
public class ProgresoController {

    private final ProgresoEntrenamientoUseCase progresoUseCase;
    private final JwtService jwtService;

    @PostMapping("/iniciar/{diaRutinaId}")
    @Operation(summary = "Iniciar un nuevo entrenamiento")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<RegistroEntrenamiento> iniciarEntrenamiento(
            @PathVariable Long diaRutinaId,
            @RequestHeader("Authorization") String authHeader) {
        Long usuarioId = extraerUsuarioId(authHeader);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(progresoUseCase.iniciarEntrenamiento(usuarioId, diaRutinaId));
    }

    @PostMapping("/{registroId}/finalizar")
    @Operation(summary = "Finalizar un entrenamiento")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<RegistroEntrenamiento> finalizarEntrenamiento(
            @PathVariable Long registroId,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody FinalizarEntrenamientoRequest request) {
        Long usuarioId = extraerUsuarioId(authHeader);
        return ResponseEntity.ok(progresoUseCase.finalizarEntrenamiento(registroId, usuarioId, request));
    }

    @GetMapping("/historial")
    @Operation(summary = "Obtener historial de entrenamientos")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<List<RegistroEntrenamiento>> obtenerHistorial(
            @RequestHeader("Authorization") String authHeader) {
        Long usuarioId = extraerUsuarioId(authHeader);
        return ResponseEntity.ok(progresoUseCase.obtenerHistorial(usuarioId));
    }

    @GetMapping("/historial/rango")
    @Operation(summary = "Obtener entrenamientos por rango de fechas")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<List<RegistroEntrenamiento>> obtenerPorRango(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        Long usuarioId = extraerUsuarioId(authHeader);
        return ResponseEntity.ok(progresoUseCase.obtenerPorRango(usuarioId, inicio, fin));
    }

    @GetMapping("/estadisticas")
    @Operation(summary = "Obtener estadísticas de entrenamiento")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<EstadisticasEntrenamiento> obtenerEstadisticas(
            @RequestHeader("Authorization") String authHeader) {
        Long usuarioId = extraerUsuarioId(authHeader);
        return ResponseEntity.ok(progresoUseCase.obtenerEstadisticas(usuarioId));
    }

    @GetMapping("/ultimo")
    @Operation(summary = "Obtener el último entrenamiento")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<RegistroEntrenamiento> obtenerUltimo(
            @RequestHeader("Authorization") String authHeader) {
        Long usuarioId = extraerUsuarioId(authHeader);
        RegistroEntrenamiento ultimo = progresoUseCase.obtenerUltimoEntrenamiento(usuarioId);
        return ultimo != null ? ResponseEntity.ok(ultimo) : ResponseEntity.notFound().build();
    }

    private Long extraerUsuarioId(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return jwtService.extractUserId(token);
    }
}
