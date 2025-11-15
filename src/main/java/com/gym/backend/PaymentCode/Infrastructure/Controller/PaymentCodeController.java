package com.gym.backend.PaymentCode.Infrastructure.Controller;

import com.gym.backend.PaymentCode.Application.Dto.PaymentCodeResponse;
import com.gym.backend.PaymentCode.Application.Mapper.PaymentCodeMapper;
import com.gym.backend.PaymentCode.Domain.*;
import com.gym.backend.PaymentCode.Infrastructure.Adapter.PaymentCodeRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment-codes")
@RequiredArgsConstructor
public class PaymentCodeController {
    private final PaymentCodeUseCase useCase;
    private final PaymentCodeMapper mapper;

    @GetMapping("/validar/{codigo}")
    public ResponseEntity<?> validar(@PathVariable String codigo) {
        try {
            var paymentCode = useCase.validarCodigo(codigo);
            return ResponseEntity.ok(mapper.toResponse(paymentCode));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/pago/{pagoId}")
    public ResponseEntity<PaymentCodeResponse> obtenerPorPago(@PathVariable Long pagoId) {
        try {
            var paymentCode = useCase.obtenerPorPagoId(pagoId);
            return ResponseEntity.ok(mapper.toResponse(paymentCode));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}