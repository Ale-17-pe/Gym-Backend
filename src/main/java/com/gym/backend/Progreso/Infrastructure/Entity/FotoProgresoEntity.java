package com.gym.backend.Progreso.Infrastructure.Entity;

import com.gym.backend.Progreso.Domain.Enum.TipoFoto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "foto_progreso")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FotoProgresoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_foto", nullable = false)
    private TipoFoto tipoFoto;

    @Column(name = "url_imagen", nullable = false, length = 500)
    private String urlImagen;

    @Column(name = "nombre_archivo", length = 255)
    private String nombreArchivo;

    @Column(name = "tamano_bytes")
    private Long tamanoBytes;

    @Column(length = 500)
    private String notas;

    @Column(name = "peso_en_foto")
    private Double pesoEnFoto;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        if (fecha == null) {
            fecha = LocalDate.now();
        }
    }
}
