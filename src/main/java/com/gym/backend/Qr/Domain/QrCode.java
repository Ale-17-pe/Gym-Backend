package com.gym.backend.Qr.Domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class QrCode {
    private String codigoPago;
    private String imagenBase64;
    private LocalDateTime fechaGeneracion;
    private int tamaño;
    private String formato;

    // Métodos de utilidad
    public String getDataUri() {
        return "data:image/png;base64," + imagenBase64;
    }

    public boolean esValido() {
        return codigoPago != null && !codigoPago.trim().isEmpty() &&
                imagenBase64 != null && !imagenBase64.trim().isEmpty();
    }
}