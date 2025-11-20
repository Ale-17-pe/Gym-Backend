package com.gym.backend.Qr.Infrastructure.Adapter;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.gym.backend.Qr.Domain.QrCode;
import com.gym.backend.Qr.Domain.QrGeneratorPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class QrGeneratorAdapter implements QrGeneratorPort {

    private static final int DEFAULT_SIZE = 300;
    private static final String DEFAULT_FORMAT = "PNG";

    @Override
    public QrCode generarQR(String codigoPago) {
        return generarQR(codigoPago, DEFAULT_SIZE, DEFAULT_SIZE);
    }

    @Override
    public QrCode generarQR(String codigoPago, int tamaño) {
        return generarQR(codigoPago, tamaño, tamaño);
    }

    @Override
    public QrCode generarQR(String codigoPago, int ancho, int alto) {
        log.debug("Generando QR para: {} con dimensiones {}x{}", codigoPago, ancho, alto);

        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);

            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    codigoPago,
                    BarcodeFormat.QR_CODE,
                    ancho,
                    alto,
                    hints
            );

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, DEFAULT_FORMAT, bos);

            String base64 = Base64.getEncoder().encodeToString(bos.toByteArray());

            return QrCode.builder()
                    .codigoPago(codigoPago)
                    .imagenBase64(base64)
                    .fechaGeneracion(LocalDateTime.now())
                    .tamaño(Math.max(ancho, alto))
                    .formato(DEFAULT_FORMAT)
                    .build();

        } catch (WriterException e) {
            log.error("Error al escribir QR para: {}", codigoPago, e);
            throw new RuntimeException("Error al generar el código QR: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error inesperado al generar QR para: {}", codigoPago, e);
            throw new RuntimeException("Error inesperado al generar el código QR", e);
        }
    }

    @Override
    public boolean validarQR(String contenido) {
        if (contenido == null || contenido.trim().isEmpty()) {
            return false;
        }

        // Validación básica - podrías expandir esto según tus necesidades
        try {
            // Intenta generar un QR pequeño para validar que el contenido es válido
            generarQR(contenido, 10, 10);
            return true;
        } catch (Exception e) {
            log.debug("Contenido inválido para QR: {}", contenido);
            return false;
        }
    }

    @Override
    public byte[] generarQRBytes(String codigoPago) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);

            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    codigoPago,
                    BarcodeFormat.QR_CODE,
                    DEFAULT_SIZE,
                    DEFAULT_SIZE,
                    hints
            );

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, DEFAULT_FORMAT, bos);

            return bos.toByteArray();

        } catch (Exception e) {
            log.error("Error al generar QR bytes para: {}", codigoPago, e);
            throw new RuntimeException("Error al generar el código QR en bytes", e);
        }
    }
}