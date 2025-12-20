package com.gym.backend.Membresias.Infrastructure.Controller;

import com.gym.backend.Membresias.Application.Dto.CrearMembresiaRequest;
import com.gym.backend.Membresias.Application.Dto.MembresiaResponse;
import com.gym.backend.Membresias.Application.Dto.MembresiaDTO;
import com.gym.backend.Membresias.Application.Mapper.MembresiaMapper;
import com.gym.backend.Membresias.Domain.Membresia;
import com.gym.backend.Membresias.Domain.MembresiaUseCase;
import com.gym.backend.Qr.Domain.QrUseCase;
import com.gym.backend.Asistencias.Domain.AsistenciaUseCase;
import com.gym.backend.Usuarios.Domain.UsuarioUseCase;
import com.gym.backend.Usuarios.Domain.Usuario;
import com.gym.backend.Planes.Domain.PlanUseCase;
import com.gym.backend.Planes.Domain.Plan;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/membresias")
@RequiredArgsConstructor
public class MembresiaController {
    private final MembresiaUseCase useCase;
    private final MembresiaMapper mapper;
    private final QrUseCase qrUseCase;
    private final AsistenciaUseCase asistenciaUseCase;
    private final UsuarioUseCase usuarioUseCase;
    private final PlanUseCase planUseCase;

    private MembresiaResponse enrichResponse(Membresia membresia) {
        MembresiaResponse response = mapper.toResponse(membresia);

        // Obtener nombre del usuario
        try {
            Usuario usuario = usuarioUseCase.obtenerConDatosCompletos(membresia.getUsuarioId());
            if (usuario != null && usuario.getPersona() != null) {
                response.setNombreUsuario(usuario.getPersona().getNombre() + " " + usuario.getPersona().getApellido());
                response.setDniUsuario(usuario.getPersona().getDni());
                response.setEmailUsuario(usuario.getEmail());
            }
        } catch (Exception e) {
            log.warn("No se pudo obtener info de usuario {}: {}", membresia.getUsuarioId(), e.getMessage());
        }

        // Obtener nombre del plan
        try {
            Plan plan = planUseCase.obtener(membresia.getPlanId());
            if (plan != null) {
                response.setNombrePlan(plan.getNombrePlan());
                response.setPrecioPlan(plan.getPrecio());
            }
        } catch (Exception e) {
            log.warn("No se pudo obtener info de plan {}: {}", membresia.getPlanId(), e.getMessage());
        }

        return response;
    }

    @PostMapping
    public ResponseEntity<MembresiaResponse> crear(@Valid @RequestBody CrearMembresiaRequest request) {
        var membresia = mapper.toDomainFromRequest(request);
        var creada = useCase.crear(membresia);
        return ResponseEntity.status(HttpStatus.CREATED).body(enrichResponse(creada));
    }

    @PostMapping("/{id}/extender")
    public ResponseEntity<MembresiaResponse> extender(@PathVariable Long id, @RequestParam Integer dias) {
        var membresia = useCase.extender(id, dias);
        return ResponseEntity.ok(enrichResponse(membresia));
    }

    @PostMapping("/{id}/suspender")
    public ResponseEntity<MembresiaResponse> suspender(@PathVariable Long id) {
        var membresia = useCase.suspender(id);
        return ResponseEntity.ok(enrichResponse(membresia));
    }

    @PostMapping("/{id}/reactivar")
    public ResponseEntity<MembresiaResponse> reactivar(@PathVariable Long id) {
        var membresia = useCase.reactivar(id);
        return ResponseEntity.ok(enrichResponse(membresia));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<MembresiaResponse> cancelar(@PathVariable Long id) {
        var membresia = useCase.cancelar(id);
        return ResponseEntity.ok(enrichResponse(membresia));
    }

    @GetMapping
    public List<MembresiaResponse> listar() {
        return useCase.listar().stream().map(this::enrichResponse).toList();
    }

    @GetMapping("/paginated")
    public Page<MembresiaResponse> listarPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return useCase.listarPaginated(pageable).map(mapper::toResponse);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<MembresiaResponse> listarPorUsuario(@PathVariable Long usuarioId) {
        return useCase.listarPorUsuario(usuarioId).stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/usuario/{usuarioId}/paginated")
    public Page<MembresiaResponse> listarPorUsuarioPaginated(
            @PathVariable Long usuarioId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return useCase.listarPorUsuarioPaginated(usuarioId, pageable).map(mapper::toResponse);
    }

    @GetMapping("/activas")
    public List<MembresiaResponse> listarActivas() {
        return useCase.listarActivas().stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/activas/paginated")
    public Page<MembresiaResponse> listarActivasPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return useCase.listarActivasPaginated(pageable).map(mapper::toResponse);
    }

    @GetMapping("/por-vencer")
    public List<MembresiaResponse> listarPorVencer() {
        return useCase.listarPorVencer().stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/vencidas")
    public List<MembresiaResponse> listarVencidas() {
        return useCase.listarVencidas().stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MembresiaResponse> obtener(@PathVariable Long id) {
        var membresia = useCase.obtener(id);
        return ResponseEntity.ok(mapper.toResponse(membresia));
    }

    @GetMapping("/activa/{usuarioId}")
    public ResponseEntity<MembresiaResponse> obtenerActiva(@PathVariable Long usuarioId) {
        var membresia = useCase.obtenerActivaPorUsuario(usuarioId);
        return membresia != null ? ResponseEntity.ok(mapper.toResponse(membresia)) : ResponseEntity.notFound().build();
    }

    @GetMapping("/verificar-acceso/{usuarioId}")
    public ResponseEntity<Map<String, Boolean>> verificarAcceso(@PathVariable Long usuarioId) {
        boolean tieneAcceso = useCase.verificarAcceso(usuarioId);
        return ResponseEntity.ok(Map.of("tieneAcceso", tieneAcceso));
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        var estadisticas = useCase.obtenerEstadisticas();
        return ResponseEntity.ok(estadisticas);
    }

    @GetMapping("/reporte/fechas")
    public List<MembresiaResponse> buscarPorRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        return useCase.buscarPorRangoFechas(fechaInicio, fechaFin)
                .stream().map(mapper::toResponse).toList();
    }

    @PostMapping("/{id}/generar-qr")
    public ResponseEntity<MembresiaDTO> generarQR(@PathVariable Long id) {
        var membresia = useCase.generarCodigoAcceso(id);

        // Generar QR en base64
        String qrBase64 = null;
        if (membresia.getCodigoAcceso() != null) {
            byte[] qrBytes = qrUseCase.generarQRBytes(membresia.getCodigoAcceso());
            qrBase64 = java.util.Base64.getEncoder().encodeToString(qrBytes);
        }

        var dto = mapper.toDTO(membresia);
        dto.setQrBase64(qrBase64);

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/validar-acceso")
    public ResponseEntity<MembresiaResponse> validarAcceso(@RequestBody Map<String, String> body) {
        String codigo = body.get("codigo");
        var membresia = useCase.validarCodigoAcceso(codigo);

        // Registrar asistencia automáticamente
        try {
            asistenciaUseCase.registrarEntrada(membresia.getUsuarioId());
        } catch (Exception e) {
            // Log pero no fallar la validación
            log.warn("No se pudo registrar asistencia para usuario {}: {}", membresia.getUsuarioId(), e.getMessage());
        }

        return ResponseEntity.ok(mapper.toResponse(membresia));
    }
}