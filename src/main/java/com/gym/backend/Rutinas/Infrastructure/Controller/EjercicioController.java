package com.gym.backend.Rutinas.Infrastructure.Controller;

import com.gym.backend.Rutinas.Application.EjercicioUseCase;
import com.gym.backend.Rutinas.Domain.Ejercicio;
import com.gym.backend.Rutinas.Domain.Enum.GrupoMuscular;
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
@RequestMapping("/api/ejercicios")
@RequiredArgsConstructor
@Tag(name = "Catálogo de Ejercicios", description = "CRUD del catálogo de ejercicios")
@SecurityRequirement(name = "bearerAuth")
public class EjercicioController {

    private final EjercicioUseCase ejercicioUseCase;

    @GetMapping
    @Operation(summary = "Listar todos los ejercicios activos")
    public ResponseEntity<List<Ejercicio>> listar() {
        return ResponseEntity.ok(ejercicioUseCase.listarActivos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un ejercicio por ID")
    public ResponseEntity<Ejercicio> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ejercicioUseCase.obtener(id));
    }

    @GetMapping("/grupo/{grupoMuscular}")
    @Operation(summary = "Buscar ejercicios por grupo muscular")
    public ResponseEntity<List<Ejercicio>> buscarPorGrupo(@PathVariable GrupoMuscular grupoMuscular) {
        return ResponseEntity.ok(ejercicioUseCase.buscarPorGrupoMuscular(grupoMuscular));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar ejercicios por nombre")
    public ResponseEntity<List<Ejercicio>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(ejercicioUseCase.buscarPorNombre(nombre));
    }

    @GetMapping("/grupos-musculares")
    @Operation(summary = "Listar todos los grupos musculares disponibles")
    public ResponseEntity<GrupoMuscular[]> listarGruposMusculares() {
        return ResponseEntity.ok(GrupoMuscular.values());
    }

    // ============ Endpoints de Admin ============

    @PostMapping
    @Operation(summary = "Crear un nuevo ejercicio (Admin)")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Ejercicio> crear(@RequestBody Ejercicio ejercicio) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ejercicioUseCase.crear(ejercicio));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un ejercicio (Admin)")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Ejercicio> actualizar(@PathVariable Long id, @RequestBody Ejercicio ejercicio) {
        return ResponseEntity.ok(ejercicioUseCase.actualizar(id, ejercicio));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar un ejercicio (Admin)")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        ejercicioUseCase.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/activar")
    @Operation(summary = "Reactivar un ejercicio (Admin)")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> activar(@PathVariable Long id) {
        ejercicioUseCase.activar(id);
        return ResponseEntity.ok().build();
    }
}
