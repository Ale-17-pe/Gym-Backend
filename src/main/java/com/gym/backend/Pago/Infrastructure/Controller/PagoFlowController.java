package com.gym.backend.Pago.Infrastructure.Controller;

import com.gym.backend.Pago.Application.Dto.CrearPagoRequest;
import com.gym.backend.Pago.Application.Dto.PagoResponse;
import com.gym.backend.Pago.Application.Mapper.PagoMapper;
import com.gym.backend.Pago.Domain.Services.PagoOrquestacionService;
import com.gym.backend.PaymentCode.Application.Mapper.PaymentCodeMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoFlowController {
    private final PagoOrquestacionService pagoOrquestacionService;
    private final PagoMapper pagoMapper;

    @PostMapping("/iniciar")
    public ResponseEntity<?> iniciarPago(@Valid @RequestBody CrearPagoRequest request) {
        try {
            var response = pagoOrquestacionService.iniciarProcesoPago(request);
            PagoResponse pagoResponse = pagoMapper.toResponse(response.getPago());
            pagoResponse.setCodigoPago(response.getPaymentCode().getCodigo());
            return ResponseEntity.status(HttpStatus.CREATED).body(pagoResponse);
        } catch (Exception e) {
            log.error("Error al iniciar pago: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/confirmar/{codigoPago}")
    public ResponseEntity<?> confirmarPago(@PathVariable String codigoPago) {
        try {
            var pago = pagoOrquestacionService.confirmarPago(codigoPago);
            return ResponseEntity.ok(pagoMapper.toResponse(pago));
        } catch (Exception e) {
            log.error("Error al confirmar pago: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    public record ErrorResponse(String message) {}
}