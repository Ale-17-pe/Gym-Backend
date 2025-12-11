package com.gym.backend.Progreso.Domain;

import com.gym.backend.Progreso.Domain.Enum.TipoFoto;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad de dominio para las fotos de progreso del cliente
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FotoProgreso {
    private Long id;
    private Long usuarioId;
    private LocalDate fecha;
    private TipoFoto tipoFoto;
    private String urlImagen; // URL de la imagen almacenada
    private String nombreArchivo;
    private Long tamanoBytes;
    private String notas;
    private Double pesoEnFoto; // Peso del cliente cuando se tomó la foto
    private LocalDateTime fechaCreacion;
}
