package com.gym.backend.Membresias.Infrastructure.Controller;

import com.gym.backend.Membresias.Application.Dto.MembresiaDTO;
import com.gym.backend.Membresias.Application.Dto.MembresiaResponse;
import com.gym.backend.Membresias.Application.Mapper.MembresiaMapper;
import com.gym.backend.Membresias.Domain.Membresia;
import com.gym.backend.Membresias.Domain.MembresiaUseCase;
import com.gym.backend.Membresias.Infrastructure.Adapter.MembresiaRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/membresias")
@RequiredArgsConstructor
public class MembresiaController {
    private final MembresiaUseCase useCase;
    private final MembresiaMapper mapper;

    @GetMapping
    public List<MembresiaResponse> listar() {
        return useCase.listar().stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<MembresiaResponse> listarPorUsuario(@PathVariable Long usuarioId) {
        return useCase.listarPorUsuario(usuarioId).stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/activa/{usuarioId}")
    public ResponseEntity<MembresiaResponse> obtenerActiva(@PathVariable Long usuarioId) {
        var membresia = useCase.obtenerActivaPorUsuario(usuarioId);
        return membresia != null ?
                ResponseEntity.ok(mapper.toResponse(membresia)) :
                ResponseEntity.notFound().build();
    }
}