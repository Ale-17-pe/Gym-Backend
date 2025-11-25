package com.gym.backend.Membresias.Application.Dto;

import com.gym.backend.Membresias.Domain.Enum.EstadoMembresia;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MembresiaDTO {
    private Long id;
    private Long usuarioId;
    private Long planId;
    private Long pagoId;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private EstadoMembresia estado;
    private LocalDate fechaCreacion;
    private LocalDate fechaActualizacion;

    // Campos para acceso al gimnasio
    private String codigoAcceso;
    private LocalDateTime codigoExpiracion;
    private String qrBase64; // QR codificado en base64 para mostrar en frontend
}