package com.gym.backend.Qr.Domain;

public interface QrGeneratorPort {
    QrCode generarQR(String codigoPago);
    QrCode generarQR(String codigoPago, int tamaño);
    QrCode generarQR(String codigoPago, int ancho, int alto);
    boolean validarQR(String contenido);
    byte[] generarQRBytes(String codigoPago);
}