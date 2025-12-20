package com.gym.backend.Fidelidad.Infrastructure.Controller;

import com.gym.backend.Fidelidad.Domain.Enum.TipoRecompensa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RecompensaRequest {
    @NotBlank(message = "El nombre es requerido")
    private String nombre;

    private String descripcion;

    @NotNull(message = "El costo en puntos es requerido")
    @Positive(message = "El costo debe ser mayor a 0")
    private Integer costoPuntos;

    @NotNull(message = "El tipo es requerido")
    private TipoRecompensa tipo;

    private BigDecimal valor; // Valor del beneficio (%, días, etc.)

    private Integer stock; // null = ilimitado

    private String imagenUrl;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;
}
