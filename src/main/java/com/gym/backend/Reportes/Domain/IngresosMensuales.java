package com.gym.backend.Reportes.Domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IngresosMensuales {
    private String mes;
    private Double total;
}
