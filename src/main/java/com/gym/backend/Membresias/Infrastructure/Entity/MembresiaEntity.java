package com.gym.backend.Membresias.Infrastructure.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "membresias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembresiaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long usuarioId;

    private Long planId;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    private String estado;
}
