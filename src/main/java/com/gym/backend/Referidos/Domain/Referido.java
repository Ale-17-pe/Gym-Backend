package com.gym.backend.Referidos.Domain;

import lombok.*;
import java.time.LocalDateTime;

/**
 * Entidad de dominio para el sistema de referidos
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Referido {
    private Long id;
    private Long referidorId; // Usuario que refiere
    private Long referidoId; // Usuario referido (nuevo)
    private String codigoReferido; // Código usado para el registro
    private EstadoReferido estado;
    private LocalDateTime fechaReferido;
    private LocalDateTime fechaCompletado; // Cuando el referido hace su primer pago
    private Integer puntosOtorgados;

    public enum EstadoReferido {
        PENDIENTE, // Referido registrado, aún no ha pagado
        COMPLETADO, // Referido hizo su primer pago
        EXPIRADO // Referido no completó dentro del plazo
    }

    /**
     * Marca el referido como completado
     */
    public void completar(int puntos) {
        this.estado = EstadoReferido.COMPLETADO;
        this.fechaCompletado = LocalDateTime.now();
        this.puntosOtorgados = puntos;
    }

    /**
     * Verifica si el referido está pendiente
     */
    public boolean estaPendiente() {
        return estado == EstadoReferido.PENDIENTE;
    }
}
