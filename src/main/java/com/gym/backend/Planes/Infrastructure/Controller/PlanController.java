package com.gym.backend.Planes.Infrastructure.Controller;


import com.gym.backend.Planes.Application.Dto.ActualizarPlanRequest;
import com.gym.backend.Planes.Application.Dto.CrearPlanRequest;
import com.gym.backend.Planes.Application.Dto.PlanResponse;
import com.gym.backend.Planes.Application.Mapper.PlanMapper;
import com.gym.backend.Planes.Domain.Exceptions.PlanDuplicateException;
import com.gym.backend.Planes.Domain.Exceptions.PlanNotFoundException;
import com.gym.backend.Planes.Domain.Exceptions.PlanValidationException;
import com.gym.backend.Planes.Domain.Plan;
import com.gym.backend.Planes.Domain.PlanUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/planes")
@RequiredArgsConstructor
@Validated
public class PlanController {

    private final PlanUseCase useCase;
    private final PlanMapper mapper;

    @PostMapping
    public ResponseEntity<PlanResponse> crear(@Valid @RequestBody CrearPlanRequest request) {
        Plan plan = mapper.toDomainFromCreateRequest(request);
        Plan creado = useCase.crear(plan);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(creado));
    }

    @GetMapping
    public List<PlanResponse> listar() {
        return useCase.listar().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @GetMapping("/paginated")
    public Page<PlanResponse> listarPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return useCase.listarPaginated(pageable)
                .map(mapper::toResponse);
    }

    @GetMapping("/activos")
    public List<PlanResponse> listarActivos() {
        return useCase.listarActivos().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @GetMapping("/activos/paginated")
    public Page<PlanResponse> listarActivosPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return useCase.listarActivosPaginated(pageable)
                .map(mapper::toResponse);
    }

    @GetMapping("/inactivos")
    public List<PlanResponse> listarInactivos() {
        return useCase.listarInactivos().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanResponse> obtener(@PathVariable Long id) {
        Plan plan = useCase.obtener(id);
        return ResponseEntity.ok(mapper.toResponse(plan));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlanResponse> actualizar(@PathVariable Long id,
                                                   @Valid @RequestBody ActualizarPlanRequest request) {
        Plan actualizado = useCase.actualizar(id, request);
        return ResponseEntity.ok(mapper.toResponse(actualizado));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<PlanResponse> desactivar(@PathVariable Long id) {
        Plan desactivado = useCase.desactivar(id);
        return ResponseEntity.ok(mapper.toResponse(desactivado));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<PlanResponse> activar(@PathVariable Long id) {
        Plan activado = useCase.activar(id);
        return ResponseEntity.ok(mapper.toResponse(activado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        useCase.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // Nuevos endpoints
    @GetMapping("/categoria/{categoria}")
    public List<PlanResponse> buscarPorCategoria(@PathVariable String categoria) {
        return useCase.buscarPorCategoria(categoria).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @GetMapping("/destacados")
    public List<PlanResponse> buscarDestacados() {
        return useCase.buscarDestacados().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @GetMapping("/precio/max/{precioMax}")
    public List<PlanResponse> buscarPorPrecioMenorIgual(@PathVariable Double precioMax) {
        return useCase.buscarPorPrecioMenorIgual(precioMax).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @GetMapping("/precio/rango")
    public Page<PlanResponse> buscarPorRangoPrecio(
            @RequestParam Double precioMin,
            @RequestParam Double precioMax,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return useCase.buscarPorRangoPrecio(precioMin, precioMax, pageable)
                .map(mapper::toResponse);
    }

    @GetMapping("/stats")
    public Map<String, Object> obtenerEstadisticas() {
        return useCase.obtenerEstadisticas();
    }

    @PatchMapping("/{id}/incrementar-contrataciones")
    public ResponseEntity<Void> incrementarContrataciones(@PathVariable Long id) {
        useCase.incrementarContrataciones(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/rating")
    public ResponseEntity<Void> actualizarRating(@PathVariable Long id,
                                                 @RequestParam Double rating) {
        useCase.actualizarRating(id, rating);
        return ResponseEntity.ok().build();
    }
}