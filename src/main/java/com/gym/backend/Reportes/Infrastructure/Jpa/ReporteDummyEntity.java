package com.gym.backend.Reportes.Infrastructure.Jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table; // Opcional, pero claro

@Entity
@Table(name = "dummy_reporte") // Opcional, pero evita que Hibernate cree una tabla si la base de datos ya está inicializada
public class ReporteDummyEntity {

    @Id // Es obligatorio que tenga una clave primaria
    private Long id;

    // No se necesita constructor ni getters/setters si no la usas para nada más
}
