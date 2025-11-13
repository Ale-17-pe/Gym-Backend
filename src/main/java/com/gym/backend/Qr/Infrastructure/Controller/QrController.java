package com.gym.backend.Qr.Infrastructure.Controller;

import com.gym.backend.Qr.Domain.QrCode;
import com.gym.backend.Qr.Domain.QrUseCase;
import com.gym.backend.Qr.Infrastructure.Adapter.QrGeneratorAdapter;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qr")
public class QrController {

    private final QrUseCase useCase;

    public QrController(QrGeneratorAdapter adapter) {
        this.useCase = new QrUseCase(adapter);
    }

    @GetMapping("/{codigoPago}")
    public QrCode generar(@PathVariable String codigoPago) {
        return useCase.generar(codigoPago);
    }
}
