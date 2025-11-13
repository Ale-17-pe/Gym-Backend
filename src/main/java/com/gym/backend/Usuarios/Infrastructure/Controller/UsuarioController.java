package com.gym.backend.Usuarios.Infrastructure.Controller;

import com.gym.backend.Usuarios.Application.Dto.UsuarioDTO;
import com.gym.backend.Usuarios.Application.Mapper.UsuarioMapper;
import com.gym.backend.Usuarios.Domain.Usuario;
import com.gym.backend.Usuarios.Domain.UsuarioUseCase;
import com.gym.backend.Usuarios.Infrastructure.Adapter.UsuarioRepositoryAdapter;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioUseCase useCase;
    private final UsuarioMapper mapper;

    public UsuarioController(UsuarioRepositoryAdapter adapter, UsuarioMapper mapper) {
        this.useCase = new UsuarioUseCase(adapter);
        this.mapper = mapper;
    }

    @PostMapping
    public UsuarioDTO crear(@RequestBody UsuarioDTO dto) {
        Usuario usuario = mapper.toDomain(dto);
        return mapper.toDTO(useCase.crear(usuario));
    }

    @GetMapping
    public List<UsuarioDTO> listar() {
        return useCase.listar().stream()
                .map(mapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public UsuarioDTO obtener(@PathVariable Long id) {
        return mapper.toDTO(useCase.obtener(id));
    }

    @PutMapping("/{id}")
    public UsuarioDTO actualizar(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        Usuario usuario = mapper.toDomain(dto);
        return mapper.toDTO(useCase.actualizar(id, usuario));
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        useCase.eliminar(id);
    }
}