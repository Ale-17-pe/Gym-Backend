package com.gym.backend.Qr.Domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class QrUseCase {

    private final QrGeneratorPort qrPort;

    public QrCode generarQR(String codigoPago) {
        log.info("Generando QR para código de pago: {}", codigoPago);

        if (codigoPago == null || codigoPago.trim().isEmpty()) {
            throw new IllegalArgumentException("El código de pago no puede estar vacío");
        }

        QrCode qrCode = qrPort.generarQR(codigoPago);
        log.info("QR generado exitosamente para: {}", codigoPago);

        return qrCode;
    }

    public QrCode generarQRCustom(String codigoPago, int tamaño) {
        log.info("Generando QR personalizado para: {} con tamaño: {}", codigoPago, tamaño);

        if (tamaño < 100 || tamaño > 1000) {
            throw new IllegalArgumentException("El tamaño del QR debe estar entre 100 y 1000 píxeles");
        }

        QrCode qrCode = qrPort.generarQR(codigoPago, tamaño);
        log.info("QR personalizado generado exitosamente");

        return qrCode;
    }

    public QrCode generarQRCustom(String codigoPago, int ancho, int alto) {
        log.info("Generando QR rectangular para: {} con dimensiones: {}x{}", codigoPago, ancho, alto);

        if (ancho < 100 || ancho > 1000 || alto < 100 || alto > 1000) {
            throw new IllegalArgumentException("Las dimensiones del QR deben estar entre 100 y 1000 píxeles");
        }

        QrCode qrCode = qrPort.generarQR(codigoPago, ancho, alto);
        log.info("QR rectangular generado exitosamente");

        return qrCode;
    }

    public byte[] generarQRBytes(String codigoPago) {
        log.info("Generando QR en bytes para: {}", codigoPago);

        byte[] qrBytes = qrPort.generarQRBytes(codigoPago);
        log.info("QR en bytes generado exitosamente, tamaño: {} bytes", qrBytes.length);

        return qrBytes;
    }

    public boolean validarContenidoQR(String contenido) {
        log.info("Validando contenido QR: {}", contenido);

        boolean esValido = qrPort.validarQR(contenido);
        log.info("Contenido QR válido: {}", esValido);

        return esValido;
    }

    public String generarDataUri(String codigoPago) {
        QrCode qrCode = generarQR(codigoPago);
        return qrCode.getDataUri();
    }
}