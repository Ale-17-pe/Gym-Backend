package com.gym.backend.Fidelidad.Application.Dto;

import com.gym.backend.Fidelidad.Domain.Enum.MotivoGanancia;
import com.gym.backend.Fidelidad.Domain.Enum.TipoTransaccion;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO para mostrar una transacción de puntos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransaccionPuntosDTO {
    private Long id;
    private TipoTransaccion tipo;
    private MotivoGanancia motivo;
    private String motivoDescripcion;
    private Integer puntos;
    private String descripcion;
    private LocalDateTime fecha;
    private boolean esGanancia;
}
