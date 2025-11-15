package com.gym.backend.Membresias.Application.Dto;


import com.gym.backend.Membresias.Domain.Enum.EstadoMembresia;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MembresiaDTO {
    private Long id;
    private Long usuarioId;
    private Long planId;
    private Long pagoId;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private EstadoMembresia estado;
}
