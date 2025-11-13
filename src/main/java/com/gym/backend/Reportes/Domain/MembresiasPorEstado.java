package com.gym.backend.Reportes.Domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MembresiasPorEstado {
    private String estado;   // ACTIVA / VENCIDA / CANCELADA
    private Long cantidad;
}
