package com.gym.backend.Clases.Infrastructure.Controller;

import com.gym.backend.Clases.Domain.HorarioClaseUseCase;
import com.gym.backend.Clases.Infrastructure.Entity.HorarioClaseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clases/horarios")
@RequiredArgsConstructor
public class HorarioClaseController {

    private final HorarioClaseUseCase useCase;

    @GetMapping
    public ResponseEntity<List<HorarioClaseEntity>> listarTodos() {
        return ResponseEntity.ok(useCase.listarTodos());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<HorarioClaseEntity>> listarActivos() {
        return ResponseEntity.ok(useCase.listarActivos());
    }

    @GetMapping("/dia/{dia}")
    public ResponseEntity<List<HorarioClaseEntity>> listarPorDia(@PathVariable Integer dia) {
        return ResponseEntity.ok(useCase.listarPorDia(dia));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HorarioClaseEntity> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(useCase.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<HorarioClaseEntity> crear(@RequestBody HorarioClaseEntity horario) {
        return ResponseEntity.ok(useCase.crear(horario));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<HorarioClaseEntity> actualizar(@PathVariable Long id,
            @RequestBody HorarioClaseEntity horario) {
        return ResponseEntity.ok(useCase.actualizar(id, horario));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        useCase.desactivar(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> activar(@PathVariable Long id) {
        useCase.activar(id);
        return ResponseEntity.ok().build();
    }
}
