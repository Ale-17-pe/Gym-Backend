package com.gym.backend.Rutinas.Infrastructure.Controller;

import com.gym.backend.Rutinas.Application.RutinaUseCase;
import com.gym.backend.Rutinas.Application.RutinaUseCase.*;
import com.gym.backend.Rutinas.Domain.DiaRutina;
import com.gym.backend.Rutinas.Domain.EjercicioRutina;
import com.gym.backend.Rutinas.Domain.Rutina;
import com.gym.backend.Shared.Security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rutinas")
@RequiredArgsConstructor
@Tag(name = "Rutinas de Ejercicio", description = "Gestión de rutinas personalizadas")
@SecurityRequirement(name = "bearerAuth")
public class RutinaController {

    private final RutinaUseCase rutinaUseCase;
    private final JwtService jwtService;

    // ============ CRUD Rutinas ============

    @PostMapping
    @Operation(summary = "Crear una nueva rutina")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<Rutina> crearRutina(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CrearRutinaRequest request) {
        Long usuarioId = extraerUsuarioId(authHeader);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(rutinaUseCase.crearRutina(usuarioId, request));
    }

    @GetMapping
    @Operation(summary = "Listar mis rutinas")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<List<Rutina>> listarMisRutinas(
            @RequestHeader("Authorization") String authHeader) {
        Long usuarioId = extraerUsuarioId(authHeader);
        return ResponseEntity.ok(rutinaUseCase.listarRutinasUsuario(usuarioId));
    }

    @GetMapping("/activa")
    @Operation(summary = "Obtener mi rutina activa")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<Rutina> obtenerRutinaActiva(
            @RequestHeader("Authorization") String authHeader) {
        Long usuarioId = extraerUsuarioId(authHeader);
        Rutina rutina = rutinaUseCase.obtenerRutinaActiva(usuarioId);
        return rutina != null ? ResponseEntity.ok(rutina) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener rutina completa con días y ejercicios")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<RutinaCompleta> obtenerRutinaCompleta(@PathVariable Long id) {
        return ResponseEntity.ok(rutinaUseCase.obtenerRutinaCompleta(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar información de una rutina")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<Rutina> actualizarRutina(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ActualizarRutinaRequest request) {
        Long usuarioId = extraerUsuarioId(authHeader);
        return ResponseEntity.ok(rutinaUseCase.actualizarRutina(id, usuarioId, request));
    }

    @PostMapping("/{id}/activar")
    @Operation(summary = "Activar una rutina (desactiva las demás)")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<Rutina> activarRutina(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        Long usuarioId = extraerUsuarioId(authHeader);
        return ResponseEntity.ok(rutinaUseCase.activarRutina(id, usuarioId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una rutina")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarRutina(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        Long usuarioId = extraerUsuarioId(authHeader);
        rutinaUseCase.eliminarRutina(id, usuarioId);
        return ResponseEntity.noContent().build();
    }

    // ============ Gestión de Días ============

    @PutMapping("/dias/{diaId}")
    @Operation(summary = "Actualizar un día de la rutina")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<DiaRutina> actualizarDia(
            @PathVariable Long diaId,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ActualizarDiaRequest request) {
        Long usuarioId = extraerUsuarioId(authHeader);
        return ResponseEntity.ok(rutinaUseCase.actualizarDiaRutina(diaId, usuarioId, request));
    }

    // ============ Gestión de Ejercicios en Rutina ============

    @PostMapping("/dias/{diaId}/ejercicios")
    @Operation(summary = "Agregar un ejercicio a un día de la rutina")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<EjercicioRutina> agregarEjercicio(
            @PathVariable Long diaId,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody AgregarEjercicioRequest request) {
        Long usuarioId = extraerUsuarioId(authHeader);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(rutinaUseCase.agregarEjercicio(diaId, usuarioId, request));
    }

    @PutMapping("/ejercicios/{ejercicioRutinaId}")
    @Operation(summary = "Actualizar configuración de un ejercicio en la rutina")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<EjercicioRutina> actualizarEjercicioRutina(
            @PathVariable Long ejercicioRutinaId,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ActualizarEjercicioRutinaRequest request) {
        Long usuarioId = extraerUsuarioId(authHeader);
        return ResponseEntity.ok(rutinaUseCase.actualizarEjercicioRutina(ejercicioRutinaId, usuarioId, request));
    }

    @DeleteMapping("/ejercicios/{ejercicioRutinaId}")
    @Operation(summary = "Eliminar un ejercicio de la rutina")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarEjercicioRutina(
            @PathVariable Long ejercicioRutinaId,
            @RequestHeader("Authorization") String authHeader) {
        Long usuarioId = extraerUsuarioId(authHeader);
        rutinaUseCase.eliminarEjercicioRutina(ejercicioRutinaId, usuarioId);
        return ResponseEntity.noContent().build();
    }

    private Long extraerUsuarioId(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return jwtService.extractUserId(token);
    }
}
