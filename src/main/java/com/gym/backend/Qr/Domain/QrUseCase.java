package com.gym.backend.Qr.Domain;

public class QrUseCase {

    private final QrGeneratorPort qrPort;

    public QrUseCase(QrGeneratorPort qrPort) {
        this.qrPort = qrPort;
    }

    public QrCode generar(String codigoPago) {
        return qrPort.generarQR(codigoPago);
    }
}
