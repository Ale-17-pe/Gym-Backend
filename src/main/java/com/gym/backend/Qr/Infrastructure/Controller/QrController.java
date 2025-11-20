package com.gym.backend.Qr.Infrastructure.Controller;

import com.gym.backend.Qr.Domain.QrCode;
import com.gym.backend.Qr.Domain.QrUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/qr")
@RequiredArgsConstructor
public class QrController {

    private final QrUseCase useCase;

    @GetMapping("/{codigoPago}")
    public ResponseEntity<QrCode> generarQR(@PathVariable String codigoPago) {
        try {
            log.info("Solicitando generación de QR para: {}", codigoPago);
            QrCode qrCode = useCase.generarQR(codigoPago);
            return ResponseEntity.ok(qrCode);
        } catch (Exception e) {
            log.error("Error al generar QR: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{codigoPago}/custom")
    public ResponseEntity<QrCode> generarQRCustom(
            @PathVariable String codigoPago,
            @RequestParam(defaultValue = "300") int tamaño) {
        try {
            QrCode qrCode = useCase.generarQRCustom(codigoPago, tamaño);
            return ResponseEntity.ok(qrCode);
        } catch (Exception e) {
            log.error("Error al generar QR custom: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{codigoPago}/rectangular")
    public ResponseEntity<QrCode> generarQRRectangular(
            @PathVariable String codigoPago,
            @RequestParam int ancho,
            @RequestParam int alto) {
        try {
            QrCode qrCode = useCase.generarQRCustom(codigoPago, ancho, alto);
            return ResponseEntity.ok(qrCode);
        } catch (Exception e) {
            log.error("Error al generar QR rectangular: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{codigoPago}/bytes")
    public ResponseEntity<byte[]> generarQRBytes(@PathVariable String codigoPago) {
        try {
            byte[] qrBytes = useCase.generarQRBytes(codigoPago);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(qrBytes.length);
            headers.set("Content-Disposition", "inline; filename=\"qr-" + codigoPago + ".png\"");

            return new ResponseEntity<>(qrBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error al generar QR bytes: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{codigoPago}/data-uri")
    public ResponseEntity<Map<String, String>> generarQRDataUri(@PathVariable String codigoPago) {
        try {
            String dataUri = useCase.generarDataUri(codigoPago);
            return ResponseEntity.ok(Map.of("dataUri", dataUri));
        } catch (Exception e) {
            log.error("Error al generar QR data URI: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/validar")
    public ResponseEntity<Map<String, Boolean>> validarQR(@RequestBody Map<String, String> request) {
        try {
            String contenido = request.get("contenido");
            boolean esValido = useCase.validarContenidoQR(contenido);
            return ResponseEntity.ok(Map.of("valido", esValido));
        } catch (Exception e) {
            log.error("Error al validar QR: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "OK",
                "service", "QR Generator",
                "timestamp", java.time.LocalDateTime.now().toString()
        ));
    }
}