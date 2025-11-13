package com.gym.backend.Qr.Infrastructure.Adapter;

import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import com.gym.backend.Qr.Domain.QrCode;
import com.gym.backend.Qr.Domain.QrGeneratorPort;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Component
public class QrGeneratorAdapter implements QrGeneratorPort {

    @Override
    public QrCode generarQR(String codigoPago) {

        try {
            int size = 300;

            var bitMatrix = new MultiFormatWriter().encode(
                    codigoPago,
                    BarcodeFormat.QR_CODE,
                    size,
                    size
            );

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", bos);

            String base64 = Base64.getEncoder().encodeToString(bos.toByteArray());

            return QrCode.builder()
                    .codigoPago(codigoPago)
                    .imagenBase64(base64)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el QR: " + e.getMessage());
        }
    }
}
