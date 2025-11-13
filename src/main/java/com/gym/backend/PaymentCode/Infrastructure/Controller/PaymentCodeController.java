package com.gym.backend.PaymentCode.Infrastructure.Controller;

import com.gym.backend.PaymentCode.Domain.*;
import com.gym.backend.PaymentCode.Infrastructure.Adapter.PaymentCodeRepositoryAdapter;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/codigo-pago")
public class PaymentCodeController {

    private final PaymentCodeUseCase useCase;

    public PaymentCodeController(PaymentCodeRepositoryAdapter adapter) {
        this.useCase = new PaymentCodeUseCase(adapter, 24);
    }

    @PostMapping("/generar/{pagoId}")
    public PaymentCode generar(@PathVariable Long pagoId) {
        return useCase.generarParaPago(pagoId);
    }

    @GetMapping("/validar/{codigo}")
    public PaymentCode validar(@PathVariable String codigo) {
        return useCase.validarCodigo(codigo);
    }

    @PatchMapping("/usar/{id}")
    public void usar(@PathVariable Long id) {
        useCase.marcarComoUsado(id);
    }
}