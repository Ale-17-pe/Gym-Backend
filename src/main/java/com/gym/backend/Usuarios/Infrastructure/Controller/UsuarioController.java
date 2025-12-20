package com.gym.backend.Usuarios.Infrastructure.Controller;

import com.gym.backend.Usuarios.Application.Dto.ActualizarUsuarioRequest;
import com.gym.backend.Usuarios.Application.Dto.CrearUsuarioRequest;
import com.gym.backend.Usuarios.Application.Dto.UsuarioResponse;
import com.gym.backend.Usuarios.Application.Mapper.UsuarioMapper;
import com.gym.backend.Usuarios.Application.RegistroUsuarioService;
import com.gym.backend.Auth.Domain.RegisterCommand;
import com.gym.backend.Usuarios.Domain.Enum.Rol;
import com.gym.backend.Usuarios.Domain.UsuarioUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioUseCase useCase;
    private final UsuarioMapper mapper;
    private final RegistroUsuarioService registroService;

    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@RequestBody CrearUsuarioRequest request) {
        log.info("Creando usuario: {}", request.getEmail());

        // Convertir CrearUsuarioRequest a RegisterCommand
        RegisterCommand command = new RegisterCommand(
                request.getNombre(),
                request.getApellido(),
                request.getGenero() != null ? request.getGenero().name() : null,
                request.getEmail(),
                request.getDni(),
                request.getTelefono(),
                request.getDireccion(),
                request.getPassword(),
                request.getRol());

        var creado = registroService.registrarUsuarioCompleto(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(creado));
    }

    @GetMapping
    public List<UsuarioResponse> listar() {
        return useCase.listar().stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/paginated")
    public Page<UsuarioResponse> listarPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return useCase.listarPaginated(pageable).map(mapper::toResponse);
    }

    @GetMapping("/{id}")
    public UsuarioResponse obtener(@PathVariable Long id) {
        return mapper.toResponse(useCase.obtenerConDatosCompletos(id));
    }

    @GetMapping("/email/{email}")
    public UsuarioResponse obtenerPorEmail(@PathVariable String email) {
        return mapper.toResponse(useCase.obtenerPorEmail(email));
    }

    @GetMapping("/dni/{dni}")
    public UsuarioResponse obtenerPorDni(@PathVariable String dni) {
        return mapper.toResponse(useCase.obtenerPorDni(dni));
    }

    @PutMapping("/{id}")
    public UsuarioResponse actualizar(@PathVariable Long id, @RequestBody ActualizarUsuarioRequest request) {
        return mapper.toResponse(useCase.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        useCase.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/desactivar")
    public UsuarioResponse desactivar(@PathVariable Long id) {
        return mapper.toResponse(useCase.desactivar(id));
    }

    @PostMapping("/{id}/activar")
    public UsuarioResponse activar(@PathVariable Long id) {
        return mapper.toResponse(useCase.activar(id));
    }

    @GetMapping("/rol/{rol}")
    public List<UsuarioResponse> listarPorRol(@PathVariable String rol) {
        Rol rolEnum = Rol.valueOf(rol.toUpperCase());
        return useCase.listarPorRol(rolEnum).stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/{id}/verificar-activo")
    public ResponseEntity<Void> verificarUsuarioActivo(@PathVariable Long id) {
        useCase.verificarUsuarioActivo(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/verificar-email")
    public ResponseEntity<UsuarioResponse> verificarEmail(@PathVariable Long id) {
        log.info("Verificando email del usuario: {}", id);
        var usuario = useCase.marcarEmailVerificado(id);
        return ResponseEntity.ok(mapper.toResponse(usuario));
    }

    @PostMapping("/{id}/enviar-reset")
    public ResponseEntity<String> enviarReset(@PathVariable Long id) {
        log.info("Enviando reset de password al usuario: {}", id);
        useCase.enviarResetPassword(id);
        return ResponseEntity.ok("Código de reset enviado al email");
    }

    @PostMapping("/{id}/avatar")
    public ResponseEntity<UsuarioResponse> subirAvatar(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, String> body) {
        String avatarUrl = body.get("avatarUrl");
        log.info("Actualizando avatar del usuario: {}", id);
        var usuario = useCase.actualizarAvatar(id, avatarUrl);
        return ResponseEntity.ok(mapper.toResponse(usuario));
    }
}