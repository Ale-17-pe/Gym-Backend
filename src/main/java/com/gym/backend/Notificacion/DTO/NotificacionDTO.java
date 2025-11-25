package com.gym.backend.Notificacion.DTO;

import com.gym.backend.Notificacion.Domain.TipoNotificacion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionDTO {
    private Long id;
    private TipoNotificacion tipo;
    private String titulo;
    private String mensaje;
    private Boolean leida;
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaCreacion;

    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaLeida;
}
