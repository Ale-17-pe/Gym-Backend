package com.gym.backend.Pago.Infrastructure.Controller;

import com.gym.backend.Pago.Application.Dto.PagoDTO;
import com.gym.backend.Pago.Application.Mapper.PagoMapper;
import com.gym.backend.Pago.Domain.PagoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {
    private final PagoUseCase useCase;
    private final PagoMapper mapper;

    @GetMapping
    public List<PagoDTO> listar() {
        return useCase.listar().stream().map(mapper::toDTO).toList();
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<PagoDTO> listarPorUsuario(@PathVariable Long usuarioId) {
        return useCase.listarPorUsuario(usuarioId).stream().map(mapper::toDTO).toList();
    }

    @GetMapping("/pendientes")
    public List<PagoDTO> listarPendientes() {
        return useCase.listarPendientes().stream().map(mapper::toDTO).toList();
    }
}
