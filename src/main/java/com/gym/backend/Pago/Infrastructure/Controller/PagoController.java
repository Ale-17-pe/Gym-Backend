package com.gym.backend.Pago.Infrastructure.Controller;

import com.gym.backend.Pago.Application.Dto.PagoDTO;
import com.gym.backend.Pago.Application.Mapper.PagoMapper;
import com.gym.backend.Pago.Domain.PagoUseCase;
import com.gym.backend.Pago.Infrastructure.Adapter.PagoRepositoryAdapter;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final PagoUseCase useCase;
    private final PagoMapper mapper;

    public PagoController(PagoRepositoryAdapter adapter, PagoMapper mapper) {
        this.useCase = new PagoUseCase(adapter);
        this.mapper = mapper;
    }

    @PostMapping
    public PagoDTO registrar(@RequestBody PagoDTO dto) {
        var pago = mapper.toDomain(dto);
        return mapper.toDTO(useCase.registrar(pago));
    }

    @PatchMapping("/{id}/estado/{estado}")
    public PagoDTO cambiarEstado(@PathVariable Long id, @PathVariable String estado) {
        return mapper.toDTO(useCase.actualizarEstado(id, estado));
    }

    @GetMapping
    public List<PagoDTO> listar() {
        return useCase.listar().stream().map(mapper::toDTO).toList();
    }

    @GetMapping("/usuario/{id}")
    public List<PagoDTO> porUsuario(@PathVariable Long id) {
        return useCase.porUsuario(id).stream().map(mapper::toDTO).toList();
    }

    @GetMapping("/membresia/{id}")
    public List<PagoDTO> porMembresia(@PathVariable Long id) {
        return useCase.porMembresia(id).stream().map(mapper::toDTO).toList();
    }
}
