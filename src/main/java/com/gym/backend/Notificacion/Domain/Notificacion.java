package com.gym.backend.Notificacion.Domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificacion")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoNotificacion tipo;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(nullable = false)
    private Boolean leida = false;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_leida")
    private LocalDateTime fechaLeida;

    @Column(name = "email_enviado", nullable = false)
    private Boolean emailEnviado = false;

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (leida == null) {
            leida = false;
        }
        if (emailEnviado == null) {
            emailEnviado = false;
        }
    }

    public void marcarComoLeida() {
        this.leida = true;
        this.fechaLeida = LocalDateTime.now();
    }
}
