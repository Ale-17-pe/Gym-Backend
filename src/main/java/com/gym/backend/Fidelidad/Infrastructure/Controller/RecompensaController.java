package com.gym.backend.Fidelidad.Infrastructure.Controller;

import com.gym.backend.Fidelidad.Application.Dto.RecompensaDTO;
import com.gym.backend.Fidelidad.Application.RecompensaUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recompensas")
@RequiredArgsConstructor
@Tag(name = "Recompensas", description = "Gestión de recompensas del sistema de fidelidad")
@SecurityRequirement(name = "bearerAuth")
public class RecompensaController {

    private final RecompensaUseCase useCase;

    @GetMapping
    @Operation(summary = "Listar todas las recompensas")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<List<RecompensaDTO>> listar() {
        return ResponseEntity.ok(useCase.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una recompensa por ID")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    public ResponseEntity<RecompensaDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(useCase.obtener(id));
    }

    @PostMapping
    @Operation(summary = "Crear nueva recompensa")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RecompensaDTO> crear(@Valid @RequestBody RecompensaRequest request) {
        return ResponseEntity.ok(useCase.crear(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar recompensa")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RecompensaDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody RecompensaRequest request) {
        return ResponseEntity.ok(useCase.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar recompensa")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        useCase.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/activar")
    @Operation(summary = "Activar recompensa")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RecompensaDTO> activar(@PathVariable Long id) {
        return ResponseEntity.ok(useCase.activar(id));
    }

    @PutMapping("/{id}/desactivar")
    @Operation(summary = "Desactivar recompensa")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RecompensaDTO> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(useCase.desactivar(id));
    }
}
