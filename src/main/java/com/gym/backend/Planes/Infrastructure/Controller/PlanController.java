package com.gym.backend.Planes.Infrastructure.Controller;

import com.gym.backend.Planes.Application.Dto.PlanDTO;
import com.gym.backend.Planes.Application.Dto.PlanMapper;
import com.gym.backend.Planes.Domain.PlanUseCase;
import com.gym.backend.Planes.Infrastructure.Adapter.PlanRepositoryAdapter;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/planes")
public class PlanController {

    private final PlanUseCase useCase;
    private final PlanMapper mapper;

    public PlanController(PlanRepositoryAdapter adapter, PlanMapper mapper) {
        this.useCase = new PlanUseCase(adapter);
        this.mapper = mapper;
    }

    @PostMapping
    public PlanDTO crear(@RequestBody PlanDTO dto) {
        return mapper.toDTO(useCase.crear(mapper.toDomain(dto)));
    }

    @GetMapping
    public List<PlanDTO> listar() {
        return useCase.listar().stream()
                .map(mapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public PlanDTO obtener(@PathVariable Long id) {
        return mapper.toDTO(useCase.obtener(id));
    }

    @PutMapping("/{id}")
    public PlanDTO actualizar(@PathVariable Long id, @RequestBody PlanDTO dto) {
        return mapper.toDTO(useCase.actualizar(id, mapper.toDomain(dto)));
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        useCase.eliminar(id);
    }
}
