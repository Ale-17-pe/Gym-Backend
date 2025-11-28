package com.gym.backend.Clases.Application.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DatosReservarClase {

    @NotNull(message = "El ID de sesión es requerido")
    private Long sesionId;

    @NotNull(message = "El ID del usuario es requerido")
    private Long usuarioId;
}
