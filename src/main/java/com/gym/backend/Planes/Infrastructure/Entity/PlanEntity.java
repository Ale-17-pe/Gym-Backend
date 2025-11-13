package com.gym.backend.Planes.Infrastructure.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "planes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombrePlan;

    @Column(length = 500)
    private String descripcion;

    private Double precio;

    private Integer duracionDias;

    private Boolean activo;
}
