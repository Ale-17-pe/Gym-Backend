package com.gym.backend.Clases.Infrastructure.Controller;

import com.gym.backend.Clases.Domain.TipoClaseUseCase;
import com.gym.backend.Clases.Infrastructure.Entity.TipoClaseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clases/tipos")
@RequiredArgsConstructor
public class TipoClaseController {

    private final TipoClaseUseCase useCase;

    @GetMapping
    public ResponseEntity<List<TipoClaseEntity>> listarTodos() {
        return ResponseEntity.ok(useCase.listarTodos());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<TipoClaseEntity>> listarActivos() {
        return ResponseEntity.ok(useCase.listarActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoClaseEntity> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(useCase.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<TipoClaseEntity> crear(@RequestBody TipoClaseEntity tipoClase) {
        return ResponseEntity.ok(useCase.crear(tipoClase));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<TipoClaseEntity> actualizar(@PathVariable Long id, @RequestBody TipoClaseEntity tipoClase) {
        return ResponseEntity.ok(useCase.actualizar(id, tipoClase));
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

    @DeleteMapping("/{id}/eliminar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        useCase.eliminar(id);
        return ResponseEntity.ok().build();
    }
}
