package com.gym.backend.Asistencias.Infrastructure.Controller;

import com.gym.backend.Asistencias.Application.Dto.AsistenciaDTO;
import com.gym.backend.Asistencias.Application.Mapper.AsistenciaMapper;
import com.gym.backend.Asistencias.Domain.AsistenciaUseCase;
import com.gym.backend.Asistencias.Infrastructure.Adapter.AsistenciaRepositoryAdapter;
import com.gym.backend.Asistencias.Infrastructure.Adapter.MembresiaValidatorAdapter;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asistencias")
public class AsistenciaController {

    private final AsistenciaUseCase useCase;
    private final AsistenciaMapper mapper;

    public AsistenciaController(
            AsistenciaRepositoryAdapter adapter, AsistenciaMapper mapper, MembresiaValidatorAdapter membreValid
    ) {
        this.useCase = new AsistenciaUseCase(adapter, membreValid);
        this.mapper = mapper;
    }

    @PostMapping("/{usuarioId}")
    public AsistenciaDTO registrar(@PathVariable Long usuarioId) {
        return mapper.toDTO(useCase.registrar(usuarioId));
    }

    @GetMapping
    public List<AsistenciaDTO> listar() {
        return useCase.listar().stream().map(mapper::toDTO).toList();
    }

    @GetMapping("/usuario/{id}")
    public List<AsistenciaDTO> porUsuario(@PathVariable Long id) {
        return useCase.porUsuario(id).stream().map(mapper::toDTO).toList();
    }
}
