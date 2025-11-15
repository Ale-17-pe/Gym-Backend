package com.gym.backend.Planes.Infrastructure.Controller;


import com.gym.backend.Planes.Application.Dto.CrearPlanRequest;
import com.gym.backend.Planes.Application.Dto.PlanResponse;
import com.gym.backend.Planes.Application.Mapper.PlanMapper;
import com.gym.backend.Planes.Domain.Exceptions.PlanDuplicateException;
import com.gym.backend.Planes.Domain.Exceptions.PlanNotFoundException;
import com.gym.backend.Planes.Domain.Exceptions.PlanValidationException;
import com.gym.backend.Planes.Domain.Plan;
import com.gym.backend.Planes.Domain.PlanUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/planes")
@RequiredArgsConstructor
public class PlanController {

    private final PlanUseCase useCase;
    private final PlanMapper mapper;

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CrearPlanRequest request) {
        try {
            Plan plan = mapper.toDomainFromCreateRequest(request);
            Plan creado = useCase.crear(plan);
            return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(creado));
        } catch (PlanDuplicateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (PlanValidationException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping
    public List<PlanResponse> listar() {
        return useCase.listar().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @GetMapping("/activos")
    public List<PlanResponse> listarActivos() {
        return useCase.listarActivos().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @GetMapping("/inactivos")
    public List<PlanResponse> listarInactivos() {
        return useCase.listarInactivos().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        try {
            Plan plan = useCase.obtener(id);
            return ResponseEntity.ok(mapper.toResponse(plan));
        } catch (PlanNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody CrearPlanRequest request) {
        try {
            Plan planExistente = useCase.obtener(id);
            Plan planActualizado = mapper.toDomainFromCreateRequest(request);
            planActualizado = Plan.builder()
                    .id(id)
                    .nombrePlan(planActualizado.getNombrePlan())
                    .descripcion(planActualizado.getDescripcion())
                    .precio(planActualizado.getPrecio())
                    .duracionDias(planActualizado.getDuracionDias())
                    .activo(planActualizado.getActivo())
                    .beneficios(planActualizado.getBeneficios())
                    .build();

            Plan actualizado = useCase.actualizar(planActualizado);
            return ResponseEntity.ok(mapper.toResponse(actualizado));
        } catch (PlanNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (PlanDuplicateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (PlanValidationException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<?> desactivar(@PathVariable Long id) {
        try {
            Plan desactivado = useCase.desactivar(id);
            return ResponseEntity.ok(mapper.toResponse(desactivado));
        } catch (PlanNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<?> activar(@PathVariable Long id) {
        try {
            Plan activado = useCase.activar(id);
            return ResponseEntity.ok(mapper.toResponse(activado));
        } catch (PlanNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            useCase.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (PlanNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    public record ErrorResponse(String message) {}
}