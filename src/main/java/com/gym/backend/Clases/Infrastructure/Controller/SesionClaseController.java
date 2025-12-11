package com.gym.backend.Clases.Infrastructure.Controller;

import com.gym.backend.Clases.Domain.SesionClaseUseCase;
import com.gym.backend.Clases.Infrastructure.Entity.SesionClaseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/clases/sesiones")
@RequiredArgsConstructor
public class SesionClaseController {

    private final SesionClaseUseCase useCase;

    @GetMapping("/semana")
    public ResponseEntity<List<SesionClaseEntity>> obtenerSemanaActual() {
        return ResponseEntity.ok(useCase.obtenerSemanaActual());
    }

    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<List<SesionClaseEntity>> obtenerPorFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(useCase.obtenerPorFecha(fecha));
    }

    @GetMapping("/rango")
    public ResponseEntity<List<SesionClaseEntity>> obtenerPorRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(useCase.obtenerPorRangoFechas(inicio, fin));
    }

    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR')")
    public ResponseEntity<Void> cancelarSesion(@PathVariable Long id, @RequestParam String motivo) {
        useCase.cancelarSesion(id, motivo);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/completar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR')")
    public ResponseEntity<Void> completarSesion(@PathVariable Long id) {
        useCase.completarSesion(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/generar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> generarSesiones(@RequestParam(defaultValue = "7") int dias) {
        useCase.generarSesionesFuturas(dias);
        return ResponseEntity.ok().build();
    }
}
