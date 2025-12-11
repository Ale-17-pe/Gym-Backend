package com.gym.backend.Fidelidad.Application.Dto;

import com.gym.backend.Fidelidad.Domain.Enum.EstadoCanje;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO para mostrar un canje realizado
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CanjeDTO {
    private Long id;
    private String nombreRecompensa;
    private String descripcionRecompensa;
    private Integer puntosUsados;
    private EstadoCanje estado;
    private String codigoCanje;
    private LocalDateTime fechaCanje;
    private LocalDateTime fechaUso;
}
