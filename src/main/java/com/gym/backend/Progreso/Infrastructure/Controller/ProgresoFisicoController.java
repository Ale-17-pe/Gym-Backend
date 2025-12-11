package com.gym.backend.Progreso.Infrastructure.Controller;

import com.gym.backend.Progreso.Application.ProgresoFisicoUseCase;
import com.gym.backend.Progreso.Application.ProgresoFisicoUseCase.*;
import com.gym.backend.Progreso.Domain.*;
import com.gym.backend.Progreso.Domain.Enum.TipoFoto;
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
import java.util.Map;

@RestController
@RequestMapping("/api/progreso")
@RequiredArgsConstructor
@Tag(name = "Progreso Físico", description = "Seguimiento de peso, medidas y fotos de progreso")
@SecurityRequirement(name = "bearerAuth")
public class ProgresoFisicoController {

    private final ProgresoFisicoUseCase progresoUseCase;
    private final JwtService jwtService;

    // ============ MEDIDAS CORPORALES ============

    @PostMapping("/medidas")
    @Operation(summary = "Registrar nuevas medidas corporales")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<MedidaCorporal> registrarMedidas(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody RegistrarMedidasRequest request) {
        Long usuarioId = extraerUsuarioId(authHeader);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(progresoUseCase.registrarMedidas(usuarioId, request));
    }

    @GetMapping("/medidas")
    @Operation(summary = "Obtener historial de medidas")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<List<MedidaCorporal>> obtenerMedidas(
            @RequestHeader("Authorization") String authHeader) {
        Long usuarioId = extraerUsuarioId(authHeader);
        return ResponseEntity.ok(progresoUseCase.obtenerHistorialMedidas(usuarioId));
    }

    @GetMapping("/medidas/rango")
    @Operation(summary = "Obtener medidas por rango de fechas")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<List<MedidaCorporal>> obtenerMedidasPorRango(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        Long usuarioId = extraerUsuarioId(authHeader);
        return ResponseEntity.ok(progresoUseCase.obtenerMedidasPorRango(usuarioId, inicio, fin));
    }

    @GetMapping("/medidas/ultima")
    @Operation(summary = "Obtener última medida registrada")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<MedidaCorporal> obtenerUltimaMedida(
            @RequestHeader("Authorization") String authHeader) {
        Long usuarioId = extraerUsuarioId(authHeader);
        MedidaCorporal medida = progresoUseCase.obtenerUltimaMedida(usuarioId);
        return medida != null ? ResponseEntity.ok(medida) : ResponseEntity.notFound().build();
    }

    @GetMapping("/medidas/comparar")
    @Operation(summary = "Comparar progreso entre dos fechas")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<ProgresoComparativo> compararProgreso(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        Long usuarioId = extraerUsuarioId(authHeader);
        return ResponseEntity.ok(progresoUseCase.compararProgreso(usuarioId, fechaInicio, fechaFin));
    }

    // ============ FOTOS DE PROGRESO ============

    @PostMapping("/fotos")
    @Operation(summary = "Registrar una foto de progreso")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<FotoProgreso> registrarFoto(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody RegistrarFotoRequest request) {
        Long usuarioId = extraerUsuarioId(authHeader);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(progresoUseCase.registrarFoto(usuarioId, request));
    }

    @GetMapping("/fotos")
    @Operation(summary = "Obtener todas las fotos de progreso")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<List<FotoProgreso>> obtenerFotos(
            @RequestHeader("Authorization") String authHeader) {
        Long usuarioId = extraerUsuarioId(authHeader);
        return ResponseEntity.ok(progresoUseCase.obtenerFotos(usuarioId));
    }

    @GetMapping("/fotos/tipo/{tipoFoto}")
    @Operation(summary = "Obtener fotos por tipo (FRENTE, LATERAL, ESPALDA)")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<List<FotoProgreso>> obtenerFotosPorTipo(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable TipoFoto tipoFoto) {
        Long usuarioId = extraerUsuarioId(authHeader);
        return ResponseEntity.ok(progresoUseCase.obtenerFotosPorTipo(usuarioId, tipoFoto));
    }

    @GetMapping("/fotos/agrupadas")
    @Operation(summary = "Obtener fotos agrupadas por fecha")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<Map<LocalDate, List<FotoProgreso>>> obtenerFotosAgrupadas(
            @RequestHeader("Authorization") String authHeader) {
        Long usuarioId = extraerUsuarioId(authHeader);
        return ResponseEntity.ok(progresoUseCase.obtenerFotosAgrupadas(usuarioId));
    }

    @GetMapping("/fotos/tipos")
    @Operation(summary = "Listar tipos de fotos disponibles")
    public ResponseEntity<TipoFoto[]> listarTiposFoto() {
        return ResponseEntity.ok(TipoFoto.values());
    }

    @DeleteMapping("/fotos/{fotoId}")
    @Operation(summary = "Eliminar una foto de progreso")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarFoto(
            @PathVariable Long fotoId,
            @RequestHeader("Authorization") String authHeader) {
        Long usuarioId = extraerUsuarioId(authHeader);
        progresoUseCase.eliminarFoto(fotoId, usuarioId);
        return ResponseEntity.noContent().build();
    }

    // ============ OBJETIVOS ============

    @PostMapping("/objetivos")
    @Operation(summary = "Crear un nuevo objetivo físico")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<ObjetivoFisico> crearObjetivo(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CrearObjetivoRequest request) {
        Long usuarioId = extraerUsuarioId(authHeader);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(progresoUseCase.crearObjetivo(usuarioId, request));
    }

    @GetMapping("/objetivos")
    @Operation(summary = "Obtener todos los objetivos")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<List<ObjetivoFisico>> obtenerObjetivos(
            @RequestHeader("Authorization") String authHeader) {
        Long usuarioId = extraerUsuarioId(authHeader);
        return ResponseEntity.ok(progresoUseCase.obtenerObjetivos(usuarioId));
    }

    @GetMapping("/objetivos/activo")
    @Operation(summary = "Obtener objetivo activo")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<ObjetivoFisico> obtenerObjetivoActivo(
            @RequestHeader("Authorization") String authHeader) {
        Long usuarioId = extraerUsuarioId(authHeader);
        ObjetivoFisico objetivo = progresoUseCase.obtenerObjetivoActivo(usuarioId);
        return objetivo != null ? ResponseEntity.ok(objetivo) : ResponseEntity.notFound().build();
    }

    @PostMapping("/objetivos/{objetivoId}/completar")
    @Operation(summary = "Marcar un objetivo como completado")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<ObjetivoFisico> completarObjetivo(
            @PathVariable Long objetivoId,
            @RequestHeader("Authorization") String authHeader) {
        Long usuarioId = extraerUsuarioId(authHeader);
        return ResponseEntity.ok(progresoUseCase.completarObjetivo(objetivoId, usuarioId));
    }

    // ============ RESUMEN ============

    @GetMapping("/resumen")
    @Operation(summary = "Obtener resumen completo del progreso")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<ResumenProgreso> obtenerResumen(
            @RequestHeader("Authorization") String authHeader) {
        Long usuarioId = extraerUsuarioId(authHeader);
        return ResponseEntity.ok(progresoUseCase.obtenerResumen(usuarioId));
    }

    private Long extraerUsuarioId(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return jwtService.extractUserId(token);
    }
}
