package com.gym.backend.Progreso.Infrastructure.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "medida_corporal")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedidaCorporalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "peso_kg")
    private Double pesoKg;

    @Column(name = "altura_m")
    private Double alturaM;

    // Medidas en centímetros
    private Double pecho;
    private Double cintura;
    private Double cadera;
    private Double cuello;

    @Column(name = "brazo_izquierdo")
    private Double brazoIzquierdo;

    @Column(name = "brazo_derecho")
    private Double brazoDerecho;

    @Column(name = "antebrazo_izquierdo")
    private Double antebrazoIzquierdo;

    @Column(name = "antebrazo_derecho")
    private Double antebrazoDerecho;

    @Column(name = "muslo_izquierdo")
    private Double musloIzquierdo;

    @Column(name = "muslo_derecho")
    private Double musloDerecho;

    @Column(name = "pantorrilla_izquierda")
    private Double pantorrillaIzquierda;

    @Column(name = "pantorrilla_derecha")
    private Double pantorrillaDerecha;

    private Double hombros;

    // Composición corporal
    @Column(name = "porcentaje_grasa")
    private Double porcentajeGrasa;

    @Column(name = "porcentaje_musculo")
    private Double porcentajeMusculo;

    @Column(name = "porcentaje_agua")
    private Double porcentajeAgua;

    @Column(name = "masa_osea")
    private Double masaOsea;

    @Column(length = 500)
    private String notas;

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
