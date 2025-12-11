package com.gym.backend.Fidelidad.Application.Dto;

import com.gym.backend.Fidelidad.Domain.Enum.TipoRecompensa;
import lombok.*;

import java.math.BigDecimal;

/**
 * DTO para mostrar una recompensa del catálogo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecompensaDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Integer costoPuntos;
    private TipoRecompensa tipo;
    private String tipoDescripcion;
    private BigDecimal valor;
    private Integer stock; // null = ilimitado
    private Boolean disponible;
    private String imagenUrl;
}
