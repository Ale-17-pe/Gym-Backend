package com.gym.backend.Referidos.Infrastructure.Controller;

import com.gym.backend.Referidos.Application.ReferidoUseCase;
import com.gym.backend.Referidos.Application.ReferidoUseCase.*;
import com.gym.backend.Shared.Security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/referidos")
@RequiredArgsConstructor
@Tag(name = "Referidos", description = "Sistema de referidos para invitar amigos")
@SecurityRequirement(name = "bearerAuth")
public class ReferidoController {

    private final ReferidoUseCase referidoUseCase;
    private final JwtService jwtService;

    @GetMapping("/mi-codigo")
    @Operation(summary = "Obtener mi código de referido para compartir")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Map<String, String>> obtenerMiCodigo(
            @RequestHeader("Authorization") String authHeader) {
        Long usuarioId = extraerUsuarioId(authHeader);
        String codigo = referidoUseCase.generarCodigoReferido(usuarioId);
        return ResponseEntity.ok(Map.of(
                "codigoReferido", codigo,
                "mensaje", "Comparte este código con tus amigos para ganar 200 puntos cuando se registren y paguen"));
    }

    @GetMapping("/mis-referidos")
    @Operation(summary = "Ver lista de personas que he referido")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<ReferidoDTO>> obtenerMisReferidos(
            @RequestHeader("Authorization") String authHeader) {
        Long usuarioId = extraerUsuarioId(authHeader);
        return ResponseEntity.ok(referidoUseCase.obtenerMisReferidos(usuarioId));
    }

    @GetMapping("/estadisticas")
    @Operation(summary = "Ver estadísticas de mis referidos")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ReferidoEstadisticasDTO> obtenerEstadisticas(
            @RequestHeader("Authorization") String authHeader) {
        Long usuarioId = extraerUsuarioId(authHeader);
        return ResponseEntity.ok(referidoUseCase.obtenerEstadisticas(usuarioId));
    }

    private Long extraerUsuarioId(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return jwtService.extractUserId(token);
    }
}
