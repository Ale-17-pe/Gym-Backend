package com.gym.backend.Fidelidad.Application.Dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

/**
 * Request para canjear puntos por una recompensa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CanjeRequest {

    @NotNull(message = "El ID de la recompensa es requerido")
    @Positive(message = "El ID de la recompensa debe ser positivo")
    private Long recompensaId;
}
