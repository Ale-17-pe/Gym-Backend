package com.gym.backend.HistorialPagos.Controller;

import com.gym.backend.HistorialPagos.Application.Dto.HistorialPagoDTO;
import com.gym.backend.HistorialPagos.Application.Mapper.HistorialPagoMapper;
import com.gym.backend.HistorialPagos.Domain.HistorialPagoUseCase;
import com.gym.backend.HistorialPagos.Infrastructure.Adapter.HistorialPagoRepositoryAdapter;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historial-pagos")
public class HistorialPagoController {

    private final HistorialPagoUseCase useCase;
    private final HistorialPagoMapper mapper;

    public HistorialPagoController(
            HistorialPagoRepositoryAdapter adapter,
            HistorialPagoMapper mapper
    ) {
        this.useCase = new HistorialPagoUseCase(adapter);
        this.mapper = mapper;
    }

    @GetMapping
    public List<HistorialPagoDTO> listar() {
        return useCase.listar().stream().map(mapper::toDTO).toList();
    }

    @GetMapping("/usuario/{id}")
    public List<HistorialPagoDTO> porUsuario(@PathVariable Long id) {
        return useCase.listarPorUsuario(id).stream().map(mapper::toDTO).toList();
    }
}
