package com.gym.backend.Clases.Infrastructure.Controller;

import com.gym.backend.Clases.Application.DTO.DatosReservarClase;
import com.gym.backend.Clases.Domain.ReservaClaseUseCase;
import com.gym.backend.Clases.Infrastructure.Entity.ReservaClaseEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clases/reservas")
@RequiredArgsConstructor
public class ReservaClaseController {

    private final ReservaClaseUseCase useCase;

    @PostMapping("/reservar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReservaClaseEntity> reservar(@Valid @RequestBody DatosReservarClase datos) {
        ReservaClaseEntity reserva = useCase.reservar(datos.getSesionId(), datos.getUsuarioId());
        return ResponseEntity.ok(reserva);
    }

    @DeleteMapping("/{id}/cancelar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> cancelar(@PathVariable Long id, @RequestParam Long usuarioId) {
        useCase.cancelar(id, usuarioId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ReservaClaseEntity>> obtenerReservasUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(useCase.obtenerReservasUsuario(usuarioId));
    }

    @GetMapping("/sesion/{sesionId}")
    @PreAuthorize("hasAnyRole('RECEPCIONISTA', 'ADMINISTRADOR', 'ENTRENADOR')")
    public ResponseEntity<List<ReservaClaseEntity>> obtenerReservasSesion(@PathVariable Long sesionId) {
        return ResponseEntity.ok(useCase.obtenerReservasSesion(sesionId));
    }

    @PutMapping("/{id}/asistencia")
    @PreAuthorize("hasAnyRole('RECEPCIONISTA', 'ADMINISTRADOR', 'ENTRENADOR')")
    public ResponseEntity<Void> marcarAsistencia(
            @PathVariable Long id,
            @RequestParam boolean asistio) {
        useCase.marcarAsistencia(id, asistio);
        return ResponseEntity.ok().build();
    }
}
