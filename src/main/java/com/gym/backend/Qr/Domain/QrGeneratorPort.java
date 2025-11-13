package com.gym.backend.Qr.Domain;

public interface QrGeneratorPort {
    QrCode generarQR(String codigoPago);
}
