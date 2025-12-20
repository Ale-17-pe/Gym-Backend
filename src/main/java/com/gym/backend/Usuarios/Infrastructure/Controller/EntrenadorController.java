package com.gym.backend.Usuarios.Infrastructure.Controller;

import com.gym.backend.Usuarios.Application.Dto.EntrenadorResponse;
import com.gym.backend.Usuarios.Application.EntrenadorUseCase;
import com.gym.backend.Usuarios.Domain.Enum.EspecialidadEntrenador;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/entrenadores")
@RequiredArgsConstructor
public class EntrenadorController {

    private final EntrenadorUseCase useCase;

    @GetMapping
    public ResponseEntity<List<EntrenadorResponse>> listarTodos() {
        return ResponseEntity.ok(useCase.listarTodos());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<EntrenadorResponse>> listarActivos() {
        return ResponseEntity.ok(useCase.listarActivos());
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<EntrenadorResponse>> listarDisponibles() {
        return ResponseEntity.ok(useCase.listarDisponibles());
    }

    @GetMapping("/mejor-valorados")
    public ResponseEntity<List<EntrenadorResponse>> listarMejorValorados() {
        return ResponseEntity.ok(useCase.listarMejorValorados());
    }

    @GetMapping("/especialidades")
    public ResponseEntity<List<Map<String, String>>> listarEspecialidades() {
        List<Map<String, String>> especialidades = Arrays.stream(EspecialidadEntrenador.values())
                .map(e -> Map.of(
                        "value", e.name(),
                        "nombre", e.getNombre(),
                        "descripcion", e.getDescripcion()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(especialidades);
    }

    @GetMapping("/usuarios-disponibles")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<EntrenadorUseCase.UsuarioDisponibleDTO>> listarUsuariosDisponibles() {
        return ResponseEntity.ok(useCase.listarUsuariosDisponibles());
    }

    @GetMapping("/especialidad/{especialidad}")
    public ResponseEntity<List<EntrenadorResponse>> listarPorEspecialidad(
            @PathVariable EspecialidadEntrenador especialidad) {
        return ResponseEntity.ok(useCase.listarPorEspecialidad(especialidad));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntrenadorResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(useCase.obtenerPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<EntrenadorResponse> obtenerPorUsuarioId(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(useCase.obtenerPorUsuarioId(usuarioId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<EntrenadorResponse> crear(@RequestBody CrearEntrenadorRequest request) {
        EntrenadorResponse entrenador = useCase.crearDesdeUsuario(
                request.getUsuarioId(),
                request.getEspecialidad(),
                request.getCertificaciones(),
                request.getExperienciaAnios(),
                request.getMaxClientes(),
                request.getBiografia());
        return ResponseEntity.ok(entrenador);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR')")
    public ResponseEntity<EntrenadorResponse> actualizar(
            @PathVariable Long id,
            @RequestBody CrearEntrenadorRequest request) {
        return ResponseEntity.ok(useCase.actualizar(
                id,
                request.getEspecialidad(),
                request.getCertificaciones(),
                request.getExperienciaAnios(),
                request.getMaxClientes(),
                request.getBiografia()));
    }

    @PutMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> activar(@PathVariable Long id) {
        useCase.activar(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        useCase.desactivar(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> contarActivos() {
        return ResponseEntity.ok(useCase.contarActivos());
    }
}
