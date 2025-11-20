package com.gym.backend.PaymentCode.Infrastructure.Controller;

import com.gym.backend.PaymentCode.Application.Dto.PaymentCodeResponse;
import com.gym.backend.PaymentCode.Application.Mapper.PaymentCodeMapper;
import com.gym.backend.PaymentCode.Domain.Enums.EstadoPaymentCode;
import com.gym.backend.PaymentCode.Domain.PaymentCodeUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/payment-codes")
@RequiredArgsConstructor
public class PaymentCodeController {
    private final PaymentCodeUseCase useCase;
    private final PaymentCodeMapper mapper;

    @PostMapping("/generar/{pagoId}")
    public ResponseEntity<PaymentCodeResponse> generarParaPago(@PathVariable Long pagoId) {
        var paymentCode = useCase.generarParaPago(pagoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(paymentCode));
    }

    @GetMapping("/validar/{codigo}")
    public ResponseEntity<PaymentCodeResponse> validar(@PathVariable String codigo) {
        var paymentCode = useCase.validarCodigo(codigo);
        return ResponseEntity.ok(mapper.toResponse(paymentCode));
    }

    @GetMapping("/pago/{pagoId}")
    public ResponseEntity<PaymentCodeResponse> obtenerPorPago(@PathVariable Long pagoId) {
        var paymentCode = useCase.obtenerPorPagoId(pagoId);
        return ResponseEntity.ok(mapper.toResponse(paymentCode));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentCodeResponse> obtenerPorId(@PathVariable Long id) {
        var paymentCode = useCase.obtenerPorId(id);
        return ResponseEntity.ok(mapper.toResponse(paymentCode));
    }

    @GetMapping
    public List<PaymentCodeResponse> listarTodos() {
        return useCase.listarTodos().stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/paginated")
    public Page<PaymentCodeResponse> listarPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return useCase.listarPaginated(pageable).map(mapper::toResponse);
    }

    @GetMapping("/estado/{estado}")
    public List<PaymentCodeResponse> listarPorEstado(@PathVariable EstadoPaymentCode estado) {
        return useCase.listarPorEstado(estado).stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/activos")
    public List<PaymentCodeResponse> listarActivos() {
        return useCase.listarActivos().stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/expirados")
    public List<PaymentCodeResponse> listarExpirados() {
        return useCase.listarExpirados().stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/por-vencer")
    public List<PaymentCodeResponse> listarPorVencer() {
        return useCase.listarPorVencer().stream().map(mapper::toResponse).toList();
    }

    @PostMapping("/{id}/usado")
    public ResponseEntity<PaymentCodeResponse> marcarComoUsado(@PathVariable Long id) {
        var paymentCode = useCase.marcarComoUsado(id);
        return ResponseEntity.ok(mapper.toResponse(paymentCode));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<PaymentCodeResponse> cancelar(@PathVariable Long id) {
        var paymentCode = useCase.cancelarCodigo(id);
        return ResponseEntity.ok(mapper.toResponse(paymentCode));
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<PaymentCodeUseCase.EstadisticasPaymentCode> obtenerEstadisticas() {
        var estadisticas = useCase.obtenerEstadisticas();
        return ResponseEntity.ok(estadisticas);
    }

    @PostMapping("/{codigo}/procesar-pago")
    public ResponseEntity<Map<String, Object>> procesarPagoConCodigo(@PathVariable String codigo) {
        try {
            var paymentCode = useCase.validarCodigo(codigo);
            var resultado = useCase.marcarComoUsado(paymentCode.getId());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Pago procesado exitosamente",
                    "paymentCode", mapper.toResponse(resultado),
                    "pagoId", resultado.getPagoId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}