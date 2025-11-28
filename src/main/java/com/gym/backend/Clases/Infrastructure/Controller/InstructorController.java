package com.gym.backend.Clases.Infrastructure.Controller;

import com.gym.backend.Clases.Domain.InstructorUseCase;
import com.gym.backend.Clases.Infrastructure.Entity.InstructorEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clases/instructores")
@RequiredArgsConstructor
public class InstructorController {

    private final InstructorUseCase useCase;

    @GetMapping
    public ResponseEntity<List<InstructorEntity>> listarTodos() {
        return ResponseEntity.ok(useCase.listarTodos());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<InstructorEntity>> listarActivos() {
        return ResponseEntity.ok(useCase.listarActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstructorEntity> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(useCase.obtenerPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<InstructorEntity> obtenerPorUsuarioId(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(useCase.obtenerPorUsuarioId(usuarioId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<InstructorEntity> crear(@RequestBody Map<String, Object> datos) {
        Long usuarioId = Long.valueOf(datos.get("usuarioId").toString());
        String especialidades = (String) datos.get("especialidades");
        String biografia = (String) datos.get("biografia");
        String fotoPerfil = (String) datos.get("fotoPerfil");

        return ResponseEntity.ok(useCase.crear(usuarioId, especialidades, biografia, fotoPerfil));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'INSTRUCTOR')")
    public ResponseEntity<InstructorEntity> actualizar(
            @PathVariable Long id,
            @RequestBody Map<String, String> datos) {
        return ResponseEntity.ok(useCase.actualizar(
                id,
                datos.get("especialidades"),
                datos.get("biografia"),
                datos.get("fotoPerfil")));
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
