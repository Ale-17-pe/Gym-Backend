package com.gym.backend.Fidelidad.Infrastructure.Controller;

import com.gym.backend.Fidelidad.Application.Dto.*;
import com.gym.backend.Fidelidad.Application.PuntosFidelidadUseCase;
import com.gym.backend.Shared.Security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fidelidad")
@RequiredArgsConstructor
@Tag(name = "Fidelidad", description = "Sistema de puntos de fidelidad")
@SecurityRequirement(name = "bearerAuth")
public class FidelidadController {

    private final PuntosFidelidadUseCase puntosFidelidadUseCase;
    private final JwtService jwtService;

    // =========================================================
    // ENDPOINTS PARA CLIENTES
    // =========================================================

    @GetMapping("/mi-balance")
    @Operation(summary = "Obtener mi balance de puntos")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<BalancePuntosDTO> obtenerMiBalance(
            @RequestHeader("Authorization") String authHeader) {
        Long usuarioId = extraerUsuarioId(authHeader);
        return ResponseEntity.ok(puntosFidelidadUseCase.obtenerBalance(usuarioId));
    }

    @GetMapping("/mis-transacciones")
    @Operation(summary = "Obtener historial de transacciones de puntos")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<List<TransaccionPuntosDTO>> obtenerMisTransacciones(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamano) {
        Long usuarioId = extraerUsuarioId(authHeader);
        return ResponseEntity.ok(puntosFidelidadUseCase.obtenerHistorial(usuarioId, pagina, tamano));
    }

    @GetMapping("/recompensas")
    @Operation(summary = "Listar recompensas disponibles")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<List<RecompensaDTO>> listarRecompensas() {
        return ResponseEntity.ok(puntosFidelidadUseCase.listarRecompensasDisponibles());
    }

    @PostMapping("/canjear")
    @Operation(summary = "Canjear puntos por una recompensa")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<CanjeDTO> canjearPuntos(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CanjeRequest request) {
        Long usuarioId = extraerUsuarioId(authHeader);
        return ResponseEntity.ok(puntosFidelidadUseCase.canjearPuntos(usuarioId, request.getRecompensaId()));
    }

    @GetMapping("/mis-canjes")
    @Operation(summary = "Obtener mis canjes realizados")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<List<CanjeDTO>> obtenerMisCanjes(
            @RequestHeader("Authorization") String authHeader) {
        Long usuarioId = extraerUsuarioId(authHeader);
        return ResponseEntity.ok(puntosFidelidadUseCase.obtenerCanjesUsuario(usuarioId));
    }

    // =========================================================
    // ENDPOINTS PARA ADMINISTRADORES
    // =========================================================

    @GetMapping("/usuarios/{usuarioId}/balance")
    @Operation(summary = "Obtener balance de puntos de un usuario (Admin)")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<BalancePuntosDTO> obtenerBalanceUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(puntosFidelidadUseCase.obtenerBalance(usuarioId));
    }

    @GetMapping("/usuarios/{usuarioId}/transacciones")
    @Operation(summary = "Obtener historial de un usuario (Admin)")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<List<TransaccionPuntosDTO>> obtenerTransaccionesUsuario(
            @PathVariable Long usuarioId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamano) {
        return ResponseEntity.ok(puntosFidelidadUseCase.obtenerHistorial(usuarioId, pagina, tamano));
    }

    @PostMapping("/usuarios/{usuarioId}/ajustar")
    @Operation(summary = "Ajustar puntos manualmente (Admin)")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<BalancePuntosDTO> ajustarPuntos(
            @PathVariable Long usuarioId,
            @Valid @RequestBody AjustePuntosRequest request) {
        return ResponseEntity.ok(puntosFidelidadUseCase.ajustarPuntos(
                usuarioId, request.getPuntos(), request.getMotivo()));
    }

    @GetMapping("/canjes/pendientes")
    @Operation(summary = "Listar canjes pendientes de entrega (Admin/Recepcionista)")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<List<CanjeDTO>> listarCanjesPendientes() {
        return ResponseEntity.ok(puntosFidelidadUseCase.listarCanjesPendientes());
    }

    @PutMapping("/canjes/{canjeId}/completar")
    @Operation(summary = "Marcar canje como completado (Admin/Recepcionista)")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<CanjeDTO> completarCanje(@PathVariable Long canjeId) {
        return ResponseEntity.ok(puntosFidelidadUseCase.completarCanje(canjeId));
    }

    @PutMapping("/canjes/{canjeId}/cancelar")
    @Operation(summary = "Cancelar canje y devolver puntos (Admin)")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<CanjeDTO> cancelarCanje(@PathVariable Long canjeId) {
        return ResponseEntity.ok(puntosFidelidadUseCase.cancelarCanje(canjeId));
    }

    @GetMapping("/canjes/codigo/{codigoCanje}")
    @Operation(summary = "Buscar canje por código (para validar en recepción)")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<?> buscarPorCodigo(@PathVariable String codigoCanje) {
        // Este endpoint permite validar un código de canje presentado por el cliente
        // Se implementará buscando el canje por código
        return ResponseEntity.ok(Map.of(
                "mensaje", "Endpoint en desarrollo",
                "codigo", codigoCanje));
    }

    // =========================================================
    // MÉTODOS AUXILIARES
    // =========================================================

    private Long extraerUsuarioId(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return jwtService.extractUserId(token);
    }
}
