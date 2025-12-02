package com.gym.backend.Reportes.Domain.Record;

import java.time.LocalDateTime;

public record IngresoDetallado(
        LocalDateTime fechaPago,
        String usuarioDni,
        String usuarioNombre,
        String planNombre,
        Double monto,
        String metodoPago,
        String estado,
        String codigoPago,
        String observacion) {
}
