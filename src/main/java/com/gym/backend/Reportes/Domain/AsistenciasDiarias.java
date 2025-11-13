package com.gym.backend.Reportes.Domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AsistenciasDiarias {
    private String fecha;
    private Long cantidad;
}
