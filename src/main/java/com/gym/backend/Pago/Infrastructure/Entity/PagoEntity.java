package com.gym.backend.Pago.Infrastructure.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long usuarioId;

    private Long membresiaId;

    private Double monto;

    private String estado;

    private LocalDateTime fechaPago;

    private String metodo_pago;
}
