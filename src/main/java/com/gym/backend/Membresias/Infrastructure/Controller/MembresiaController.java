package com.gym.backend.Membresias.Infrastructure.Controller;

import com.gym.backend.Membresias.Application.Dto.MembresiaDTO;
import com.gym.backend.Membresias.Application.Mapper.MembresiaMapper;
import com.gym.backend.Membresias.Domain.Membresia;
import com.gym.backend.Membresias.Domain.MembresiaUseCase;
import com.gym.backend.Membresias.Infrastructure.Adapter.MembresiaRepositoryAdapter;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/membresias")
public class MembresiaController {

    private final MembresiaUseCase useCase;
    private final MembresiaMapper mapper;

    public MembresiaController(MembresiaRepositoryAdapter adapter, MembresiaMapper mapper) {
        this.useCase = new MembresiaUseCase(adapter);
        this.mapper = mapper;
    }

    @PostMapping("/crear/{duracion}")
    public MembresiaDTO crear(
            @PathVariable int duracion,
            @RequestBody MembresiaDTO dto
    ) {
        Membresia membresia = mapper.toDomain(dto);
        return mapper.toDTO(useCase.crear(membresia, duracion));
    }

    @GetMapping
    public List<MembresiaDTO> listar() {
        return useCase.listar().stream()
                .map(mapper::toDTO)
                .toList();
    }

    @GetMapping("/usuario/{id}")
    public List<MembresiaDTO> porUsuario(@PathVariable Long id) {
        return useCase.porUsuario(id).stream().map(mapper::toDTO).toList();
    }

    @GetMapping("/activa/{id}")
    public MembresiaDTO activa(@PathVariable Long id) {
        var m = useCase.activaPorUsuario(id);
        return m == null ? null : mapper.toDTO(m);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        useCase.eliminar(id);
    }
}
