package com.gym.backend.HistorialMembresias.Infrastructure.Controller;

import com.gym.backend.HistorialMembresias.Application.Dto.HistorialMembresiaDTO;
import com.gym.backend.HistorialMembresias.Application.Mapper.HistorialMembresiaMapper;
import com.gym.backend.HistorialMembresias.Domain.HistorialMembresiaUseCase;
import com.gym.backend.HistorialMembresias.Infrastructure.Adapter.HistorialMembresiaRepositoryAdapter;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historial-membresias")
public class HistorialMembresiaController {

    private final HistorialMembresiaUseCase useCase;
    private final HistorialMembresiaMapper mapper;

    public HistorialMembresiaController(
            HistorialMembresiaRepositoryAdapter adapter,
            HistorialMembresiaMapper mapper
    ) {
        this.useCase = new HistorialMembresiaUseCase(adapter);
        this.mapper = mapper;
    }

    @GetMapping
    public List<HistorialMembresiaDTO> listar() {
        return useCase.listar().stream().map(mapper::toDTO).toList();
    }

    @GetMapping("/usuario/{id}")
    public List<HistorialMembresiaDTO> porUsuario(@PathVariable Long id) {
        return useCase.listarPorUsuario(id).stream().map(mapper::toDTO).toList();
    }
}
