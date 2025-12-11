package com.gym.backend.Fidelidad.Application.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Request para ajustar puntos manualmente (admin)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AjustePuntosRequest {

    @NotNull(message = "La cantidad de puntos es requerida")
    private Integer puntos; // Positivo = sumar, Negativo = restar

    @NotBlank(message = "El motivo del ajuste es requerido")
    private String motivo;
}
