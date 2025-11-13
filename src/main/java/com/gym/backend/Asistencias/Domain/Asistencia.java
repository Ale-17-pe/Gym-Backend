package com.gym.backend.Asistencias.Domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class Asistencia {

    private Long id;
    private Long usuarioId;
    private LocalDateTime fechaHora;
}
