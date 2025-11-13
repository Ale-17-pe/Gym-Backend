package com.gym.backend.Qr.Domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class QrCode {

    private String codigoPago; // El código de pago: GYM-91A23F
    private String imagenBase64; // QR en Base64
}
