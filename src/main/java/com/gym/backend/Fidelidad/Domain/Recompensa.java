package com.gym.backend.Fidelidad.Domain;

import com.gym.backend.Fidelidad.Domain.Enum.TipoRecompensa;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad de dominio que representa una recompensa del catálogo
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recompensa {
    private Long id;
    private String nombre;
    private String descripcion;
    private Integer costoPuntos;
    private TipoRecompensa tipo;
    private BigDecimal valor; // Valor del beneficio (%, días, etc.)
    private Integer stock; // null = ilimitado
    private Boolean activo;
    private String imagenUrl;
    private LocalDate fechaInicio;
    private LocalDate fechaFin; // null = sin límite
    private LocalDateTime fechaCreacion;

    /**
     * Verifica si la recompensa está disponible para canjear
     */
    public boolean estaDisponible() {
        if (!activo)
            return false;

        LocalDate hoy = LocalDate.now();

        // Verificar fechas de vigencia
        if (fechaInicio != null && hoy.isBefore(fechaInicio))
            return false;
        if (fechaFin != null && hoy.isAfter(fechaFin))
            return false;

        // Verificar stock
        if (stock != null && stock <= 0)
            return false;

        return true;
    }

    /**
     * Reduce el stock en 1 (si tiene stock limitado)
     */
    public void reducirStock() {
        if (stock != null && stock > 0) {
            this.stock--;
        }
    }

    /**
     * Aumenta el stock en 1 (por cancelación de canje)
     */
    public void aumentarStock() {
        if (stock != null) {
            this.stock++;
        }
    }

    /**
     * Obtiene el valor como porcentaje (para descuentos)
     */
    public int getValorPorcentaje() {
        return valor != null ? valor.intValue() : 0;
    }

    /**
     * Obtiene el valor como días (para extensiones)
     */
    public int getValorDias() {
        return valor != null ? valor.intValue() : 0;
    }
}
