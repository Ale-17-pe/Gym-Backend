package com.gym.backend.Usuarios.Infrastructure.Controller;

import com.gym.backend.Usuarios.Domain.Enum.EspecialidadEntrenador;
import lombok.Data;

@Data
public class CrearEntrenadorRequest {
    private Long usuarioId;
    private EspecialidadEntrenador especialidad;
    private String certificaciones;
    private Integer experienciaAnios;
    private Integer maxClientes;
    private String biografia;
}
